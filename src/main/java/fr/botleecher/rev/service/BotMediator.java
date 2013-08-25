package fr.botleecher.rev.service;

import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.BotListener;
import fr.botleecher.rev.IrcConnectionListener;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.tools.TextWriter;
import org.pircbotx.User;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public interface BotMediator extends IrcConnectionListener, TextWriter, BotListener {

    String getServer();

    String getChannel();

    List<BotLeecher> getAllBots();

    List<User> getUsers();

    void connect(String server, String channel) throws InterruptedException;

    void getList(String user, boolean refresh);

    List<Pack> getCurrentPackList(String user);

    void getPack(String user, int pack);

    int getProgress(String user);

    long getTransfertRate(String user);

    Date getEstimatedEnd(String user);

    List<String> getServers();

    void addServer(String server);

    List<String> getChannels();

    void addChannel(String channel);

    String getSaveDir();

    void setSaveDir(String path);

    List<String> getNicks();

    void setKeywords(String keywords);

    List<String> getKeywords();

    void setNicks(String nicks);

    String getCurrentFile(String user);

    void createLeecher(String user);

    void cancel(String user);

    void removeLeecher(String user);
}
