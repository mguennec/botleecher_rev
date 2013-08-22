package fr.botleecher.rev;

import org.pircbotx.User;
import org.pircbotx.hooks.Listener;

import java.util.List;


/**
 * 
 * @author francisdb
 */
public interface IrcConnectionListener extends Listener {
    /**
     * 
     * @param channel 
     * @param users 
     */
    void userListLoaded(String channel, List<User> users);
    
    void disconnected();
}