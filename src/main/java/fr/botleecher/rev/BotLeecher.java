/*
 * BotLeecher.java
 *
 * Created on April 5, 2007, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.botleecher.rev;

import com.google.inject.Inject;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.model.PackList;
import fr.botleecher.rev.model.PackStatus;
import fr.botleecher.rev.service.PackListReader;
import fr.botleecher.rev.service.Settings;
import org.apache.commons.lang3.time.DateUtils;
import org.pircbotx.User;
import org.pircbotx.dcc.ReceiveFileTransfer;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author francisdb
 */
public class BotLeecher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);
    private final ExecutorService executorService;
    private final LeecherQueue queue;
    private final Settings settings;
    private final PackListReader packListReader;
    private User botUser;
    private String description;
    private IrcConnection connection;
    private boolean leeching;
    private boolean downloading;
    private boolean listRequested;
    private int counter = 1;
    private ReceiveFileTransfer currentTransfer;
    private long fileSize;
    private File listFile;
    private List<BotListener> listeners;
    private Date startTime;
    private PackList packList;

    /**
     * Creates a new instance of BotLeecher
     *
     * @param user
     * @param connection
     */
    @Inject
    public BotLeecher(User user, IrcConnection connection, final Settings settings, final PackListReader packListReader) {
        this.settings = settings;
        this.packListReader = packListReader;

        this.botUser = user;
        this.connection = connection;
        this.currentTransfer = null;
        this.leeching = false;
        this.listRequested = false;
        this.description = "";
        this.listeners = new Vector<>();
        this.queue = new LeecherQueue();
        executorService = Executors.newFixedThreadPool(1);
    }

    public void start() {
        requestPackList();
        executorService.submit(this.queue);
    }

    public void requestPackList() {
        queue.add(1);
    }

    public void cancel() {
        queue.cancel();
    }

    public PackList getPackList() {
        return packList;
    }

    public User getUser() {
        return botUser;
    }

    /**
     * @param transfer
     */
    public void onIncomingFileTransfer(IncomingFileTransferEvent transfer) throws Exception {
        queue.onIncomingFileTransfer(transfer);
    }

    private void downloadFinished(IncomingFileTransferEvent transfer) {
        changeState(transfer.getRawFilename(), PackStatus.DOWNLOADED);
        LOGGER.info("FINISHED:\t Transfer finished for " + transfer.getSafeFilename());
    }

    /**
     * @param listener
     */
    public void addListener(BotListener listener) {
        listeners.add(listener);
    }

    /**
     * @return
     */
    public boolean isLeeching() {
        return leeching;
    }

    /**
     * @return
     */
    public IrcConnection getConnection() {
        return connection;
    }

    /**
     * @param connection
     */
    public void setConnection(IrcConnection connection) {
        this.connection = connection;
    }

    /**
     * @return
     */
    public int getCounter() {
        return this.counter;
    }

    /**
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void requestPack(int nr) {
        queue.add(nr);
        changeState(nr, PackStatus.QUEUED);
    }

    private void fireListEvent() {
        for (BotListener listener : listeners) {
            listener.packListLoaded(botUser.getNick(), Collections.unmodifiableList(packList.getPacks()));
        }
    }

    public ReceiveFileTransfer getCurrentTransfer() {
        return currentTransfer;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getTransfertRate() {
        final long rate;
        if (currentTransfer == null) {
            rate = 0;
        } else {
            final long currentData = currentTransfer == null ? 0 : currentTransfer.getBytesTransfered();
            final long diff = (new Date().getTime() - startTime.getTime()) / 1000;
            rate = currentData / diff;
        }
        return rate;
    }

    public Date getEstimatedEnd() {
        final Date end;
        final long transferRate = getTransfertRate();
        if (transferRate == 0) {
            end = null;
        } else {
            final long data = getFileSize() - (currentTransfer == null ? 0 : currentTransfer.getBytesTransfered());
            final long time = data / transferRate;
            end = startTime == null ? null : DateUtils.addSeconds(startTime, (int) time);
        }

        return end;
    }

    public long getCurrentState() {
        long state = 0;
        if (downloading && currentTransfer != null) {
            state = currentTransfer.getBytesTransfered();
        }

        return state;
    }

    public void stop() {
        queue.stop();
        executorService.shutdownNow();
    }

    public void changeState(final String name, final PackStatus status) {
        if (packList != null) {
            changeState(packList.getByName(name), status);
        }
    }

    public void changeState(final Integer nb, final PackStatus status) {
        if (packList != null) {
            changeState(packList.getByNumber(nb), status);
        }
    }

    public void changeState(final Pack pack, final PackStatus status) {
        if (pack != null && status != null) {
            pack.setStatus(status);
            fireListEvent();
        }
    }

    public class LeecherQueue implements Runnable {

        private boolean working = true;
        private boolean cancel = false;

        private BlockingQueue<Integer> internalQueue = new LinkedBlockingQueue<>();

        public void stop() {
            working = false;
        }


        public boolean add(Integer nr) {
            final boolean retVal;
            if (internalQueue.contains(nr)) {
                retVal = false;
            } else {
                retVal = internalQueue.add(nr);
            }
            return retVal;
        }

        @Override
        public void run() {
            while (working) {
                try {
                    final Integer nr = internalQueue.take();
                    askPack(nr);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        private synchronized void askPack(Integer nr) {
            if (cancel) {
                cancel = false;
                changeState(nr, PackStatus.AVAILABLE);
            } else if (nr != null) {
                if (nr.equals(1)) {
                    listRequested = true;
                }
                botUser.send().message("XDCC SEND " + nr);
            }
        }

        /**
         * @param transfer
         */
        public synchronized void onIncomingFileTransfer(IncomingFileTransferEvent transfer) throws Exception {
            if (listRequested) {
                try {
                    listFile = java.io.File.createTempFile("list", ".tmp");
                    listFile.deleteOnExit();
                    transfer.accept(listFile).transfer();
                    LOGGER.info("LIST:\t List received for " + transfer.getUser().getNick());
                    listRequested = false;
                    packList = packListReader.readPacks(listFile);
                    for (String message : packList.getMessages()) {
                        description += message + "\n";
                    }
                    fireListEvent();
                } catch (IOException ex) {
                    LOGGER.error("Error while receiving file!", ex);
                }
            } else {
                // TODO create subfolder per bot
                File saveFile = new File(settings.getSaveFolder(), transfer.getSafeFilename());
                changeState(transfer.getRawFilename(), PackStatus.DOWNLOADING);
                try {
                    currentTransfer = transfer.accept(saveFile);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                fileSize = transfer.getFilesize();
                LOGGER.info("INCOMING:\t" + currentTransfer.getFile().toString() + " " +
                        transfer.getFilesize() + " bytes");

                //if file exists cut one 8bytes off to make transfer go on
                if (saveFile.exists() && (transfer.getFilesize() == saveFile.length())) {
                    LOGGER.info("EXISTS:\t try to close connection");

                    //FileImageInputStream fis = new FileInputStream
                } else {
                    LOGGER.info("SAVING TO:\t" + saveFile.toString());
                    try {
                        downloading = true;
                        startTime = new Date();
                        currentTransfer.transfer();
                        downloadFinished(transfer);
                    } catch (IOException e) {
                        changeState(transfer.getRawFilename(), PackStatus.AVAILABLE);
                        saveFile.delete();
                        LOGGER.error(e.getMessage(), e);
                    }
                    startTime = null;
                    downloading = false;
                    currentTransfer = null;
                }
            }
        }

        public void cancel() {
            final List<Integer> list = new ArrayList<>();
            cancel = true;
            internalQueue.drainTo(list);
            for (Integer id : list) {
                changeState(id, PackStatus.AVAILABLE);
            }
            botUser.send().message("XDCC CANCEL");
        }
    }


}
