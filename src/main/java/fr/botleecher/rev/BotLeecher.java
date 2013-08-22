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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author francisdb
 */
public class BotLeecher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);
    
    private User botUser;
    
    private String description;
    
    private IrcConnection connection;
    
    private boolean leeching;
    
    private boolean downloading;
    
    private boolean listRequested;
    
    private int counter = 1;
    
    private ReceiveFileTransfer currentTransfer;

    private long fileSize;

    private String lastNotice;
    
    private File listFile;
    
    private List<BotListener> listeners;
    
    private final BlockingQueue<Pack> queue;

    private final Settings settings;
    private final PackListReader packListReader;

    private Date startTime;

    private PackList packList;
    
    /**
     * Creates a new instance of BotLeecher
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
        this.lastNotice = "";
        this.description = "";
        this.listeners = new Vector<BotListener>();
        this.queue = new LinkedBlockingQueue<Pack>();
    }
    
    public void start(){
        requestPackList();
    }
    
    public void requestPackList(){
        listRequested = true;

        botUser.send().message("XDCC SEND 1");
    }

    public void cancel() {
        botUser.send().message("XDCC CANCEL");
    }
    

    public PackList getPackList() {
        return packList;
    }

    public User getUser() {
        return botUser;
    }

    /**
     *
     * @param transfer
     */
    public synchronized void onIncomingFileTransfer(IncomingFileTransferEvent transfer) {
        if(listRequested){
            try {
                listFile = java.io.File.createTempFile("list", ".tmp");
                listFile.deleteOnExit();
                transfer.accept(listFile).transfer();
                LOGGER.info("LIST:\t List received for "+transfer.getUser().getNick());
                listRequested = false;
                packList = packListReader.readPacks(listFile);
                for(String message:packList.getMessages()){
                    this.description  += message + "\n";
                }
                List<Pack> packs = Collections.unmodifiableList(packList.getPacks());
                for(BotListener listener:listeners){
                    listener.packListLoaded(this.botUser.getNick(), packs);
                }
            } catch (IOException ex) {
                LOGGER.error("Error while receiving file!", ex);
            }
        }else{
            // TODO create subfolder per bot
            File saveFile = new File(settings.getSaveFolder(), transfer.getSafeFilename());
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
                    saveFile.delete();
                    LOGGER.error(e.getMessage(), e);
                }
                startTime = null;
                downloading = false;
                currentTransfer = null;
            }
        }
    }

    private void downloadFinished(IncomingFileTransferEvent transfer) {
        //final Telegraph telegraph = new Telegraph("Download completed", transfer.getSafeFilename() + " has been downloaded (" + currentTransfer.getBytesTransfered() + "/" + transfer.getFilesize() + ")", TelegraphType.NOTIFICATION_DONE, WindowPosition.TOPLEFT, 10000);
        //final TelegraphQueue queue = new TelegraphQueue();
        //queue.add(telegraph);
        LOGGER.info("FINISHED:\t Transfer finished for " + transfer.getSafeFilename());
    }

    /**
     *
     * @param listener
     */
    public void addListener(BotListener listener){
        listeners.add(listener);
    }

    public void onNotice(String notice) {
        if (notice.contains("Invalid Pack Number")) {
            LOGGER.info("DONE LEECHING BOT "+botUser.getNick());
            leeching = false;
        }
        
        if (notice.contains("point greater")) {
            LOGGER.info("EXISTS:\t try to close connection");
            /*try {
               // curentTransfer.close();
            } catch (IOException e) {
                // Nothing
            }*/
            //this.sendMessage(botName,"XDCC remove");
        }
        
        if(notice.contains("Closing Connection: Pack file changed")){
            // TODO do something here (retry?)
            LOGGER.info("PACK file changed");
        }
        
        lastNotice = notice;
    }
    /**
     *
     * @return
     */
    public boolean isLeeching() {
        return leeching;
    }
    
    /**
     *
     * @param connection
     */
    public void setConnection(IrcConnection connection) {
        this.connection = connection;
    }
    
    /**
     *
     * @return
     */
    public IrcConnection getConnection() {
        return connection;
    }
    
    /**
     *
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
    
    /**
     *
     * @return
     */
    public int getCounter() {
        return this.counter;
    }
    
    /**
     *
     * @return
     */
    public String getLastNotice() {
        return lastNotice;
    }
    
    /**
     * 
     * @return 
     */
    public String getDescription() {
        return description;
    }

    public void requestPack(int nr) {
        botUser.send().message("XDCC SEND " + nr);
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
        final long transfertRate = getTransfertRate();
        if (transfertRate == 0) {
            end = null;
        } else {
            final long data = getFileSize() - (currentTransfer == null ? 0 : currentTransfer.getBytesTransfered());
            final long time = data / transfertRate;
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

    
}
