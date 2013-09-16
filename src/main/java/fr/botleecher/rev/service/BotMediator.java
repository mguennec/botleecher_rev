package fr.botleecher.rev.service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.BotListener;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.IrcConnectionListener;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.model.PackList;
import fr.botleecher.rev.tools.DualOutputStream;
import fr.botleecher.rev.tools.TextWriter;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author francisdb
 */
@Singleton
public class BotMediator extends ListenerAdapter implements IrcConnectionListener, TextWriter, BotListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotMediator.class);
    private IrcConnection ircConnection;

    @Inject
    private Injector injector;

    @Inject
    private Settings settings;

    private String server;
    private String channel;

    private Map<String, User> users = new HashMap<>();

    @Inject
    private EventMediatorService service;

    @Override
    public void disconnected() {
        if (ircConnection != null) {
            ircConnection = null;
            users.clear();
            service.sendUserList(Collections.<String>emptyList());
            writeText("Disconnected");
        }
    }

    public List<BotLeecher> getAllBots() {
        return ircConnection == null ? new ArrayList<BotLeecher>() : ircConnection.getAllBots();
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        final String message = event.getMessage();
        if (message != null) {
            for (String keyword : settings.getKeywords()) {
                if (message.toLowerCase().contains(keyword.toLowerCase())) {
                    writeText(event.getUser().getNick() + " : " + message);
                    break;
                }
            }
        }
    }

    @Override
    public void onIncomingFileTransfer(final IncomingFileTransferEvent event) {
        new Thread(new Runnable() {
            public void run() {
                if (getIrcConnection() != null) {
                    writeText(event.getUser().getNick() + " : Downloading " + event.getSafeFilename(), EventMediatorService.MessageType.DOWNLOAD);
                    try {
                        getIrcConnection().getBotLeecher(event.getUser().getNick()).onIncomingFileTransfer(event);
                    } catch (Exception e) {
                        writeError(e.getMessage());
                    }
                    writeText(event.getUser().getNick() + " : Download complete " + event.getSafeFilename(), EventMediatorService.MessageType.DOWNLOAD);
                }
            }
        }).start();
    }

    @Override
    public void onUserList(UserListEvent event) {
        final ArrayList<User> list = new ArrayList<>(event.getUsers());
        Collections.sort(list, new UserComparator());
        userListLoaded(event.getChannel().getName(), list);
    }

    /**
     *
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        LOGGER.info("DISCONNECT:\tDisconnected from server");
        disconnected();
    }

    public BotMediator() {
        //redirectOutputStreams();
    }


    private void redirectOutputStreams() {
        PrintStream oldStream = System.out;
        PrintStream aPrintStream = new PrintStream(new DualOutputStream(oldStream, this));
        System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
    }

    @Override
    public void userListLoaded(final String channel, final List<User> users) {
        final List<String> userList = new ArrayList<>();
        this.users.clear();
        for (User user : users) {
            userList.add(user.getNick());
            this.users.put(user.getNick(), user);
        }
        service.sendUserList(userList);
    }

    public List<User> getUsers() {
        final ArrayList<User> list = new ArrayList<>(users.values());
        Collections.sort(list, new UserComparator());
        return list;
    }

    public void writeText(final String text, final EventMediatorService.MessageType type) {
        service.sendMessage(text, type);
    }

    @Override
    public void writeText(final String text) {
        writeText(text, EventMediatorService.MessageType.INFO);
    }

    @Override
    public void writeError(final String text) {
        writeText(text, EventMediatorService.MessageType.ERROR);
    }

    /**
     * Connects to the irc network
     */
    public void connect(final String server, final String channel) throws Exception {
        if (ircConnection != null) {
            disconnected();
        }
        if (!getServers().contains(server)) {
            addServer(server);
        }
        if (!getChannels().contains(channel)) {
            addChannel(channel);
        }
        this.server = server;
        this.channel = channel;
        ircConnection = injector.getInstance(IrcConnection.class);
        new Thread(new Runnable() {
            public void run() {
                try {
                    ircConnection.startBot();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                } catch (IrcException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    public void getList(final String user, final boolean refresh) {
        BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher == null) {
            createLeecher(user);
        } else {
            if (refresh || botLeecher.getPackList() == null) {
                botLeecher.requestPackList();
            } else {
                packListLoaded(user, botLeecher.getPackList().getPacks());
            }
        }
    }

    public List<Pack> getCurrentPackList(String user) {
        final List<Pack> packs = new ArrayList<>();
        final BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            final PackList packList = botLeecher.getPackList();
            if (packList != null) {
                packs.addAll(packList.getPacks());
            }
        }
        return packs;
    }

    public void getPack(final String user, final int pack) {
        final BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            writeText(user + " : Sending Request for pack #" + pack, EventMediatorService.MessageType.REQUEST);
            botLeecher.requestPack(pack);
        }
    }

    public int getProgress(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        final int progress;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            progress = (int) (((double) botLeecher.getCurrentState() / (double) botLeecher.getFileSize()) * 100);
        } else {
            progress = 0;
        }
        return progress;
    }

    public long getTransfertRate(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        final long rate;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            rate = botLeecher.getTransfertRate();
        } else {
            rate = 0;
        }
        return rate;
    }

    public Date getEstimatedEnd(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        final Date end;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            end = botLeecher.getEstimatedEnd();
        } else {
            end = null;
        }
        return end;
    }

    public List<String> getServers() throws Exception {
        return settings.getServers();
    }

    public void addServer(final String server) throws Exception {
        settings.addServer(server);
    }

    public List<String> getChannels() throws Exception {
        return settings.getChannels();
    }

    public void addChannel(final String channel) throws Exception {
        settings.addChannel(channel);
    }

    public String getSaveDir() throws Exception {
        return settings.getSaveFolder();
    }

    public void setSaveDir(final String path) throws Exception {
        settings.setSaveFolder(path);
    }

    public List<String> getNicks() throws Exception {
        return settings.getNicks();
    }

    public void setNicks(final List<String> nicks) throws Exception {
        settings.setNicks(nicks);
    }

    public List<String> getKeywords() throws Exception {
        return settings.getKeywords();
    }

    public void setKeywords(final List<String> keywords) throws Exception {
        settings.setKeywords(keywords);
    }

    public String getCurrentFile(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        final String file;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null && botLeecher.getCurrentTransfer().getFile() != null) {
            file = botLeecher.getCurrentTransfer().getFile().getName();
        } else {
            file = "";
        }
        return file;
    }

    public void cancel(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            botLeecher.cancel();
        }
    }

    public void createLeecher(final String user) {
        final User userObject = users.get(user);
        if (userObject != null) {
            ircConnection.makeLeecher(userObject);
        }
    }

    /**
     * Todo refactor, this should stay private
     *
     * @return
     */
    private IrcConnection getIrcConnection() {
        return ircConnection;
    }

    public String getServer() {
        return server;
    }

    public String getChannel() {
        return channel;
    }

    public void removeLeecher(final String user) {
        final User userObject = users.get(user);
        if (userObject != null) {
            ircConnection.removeLeecher(userObject);
        }
    }

    /**
     * Triggered when the pack list has been loaded
     *
     * @param packList
     */
    @Override
    public void packListLoaded(final String botName, List<Pack> packList) {
        service.sendPack(botName, packList);
    }


    private static class UserComparator implements Comparator<User>, Serializable {

        public int compare(User o1, User o2) {
            return o1.getNick().compareToIgnoreCase(o2.getNick());
        }
    }

}
