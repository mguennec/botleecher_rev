package fr.botleecher.rev.service;

import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.IrcConnection;
import org.pircbotx.User;

/**
 *
 * @author fdb
 */
public interface BotLeecherFactory {
    
    /**
     * Creates a botleecher
     * @param user
     * @return
     */
    BotLeecher getBotLeecher(User user, IrcConnection connection);


}
