package fr.botleecher.rev;

import com.google.inject.Inject;
import fr.botleecher.rev.service.BotMediator;
import fr.botleecher.rev.service.BotLeecherFactory;
import fr.botleecher.rev.service.NicknameProvider;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 *
 * @author francisdb
 */
public class IrcConnection extends PircBotX {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);

    
    private final List<IrcConnectionListener> listeners;
    //private PropertyChangeSupport propertyChangeSupport;
    
    private Map<String,BotLeecher> leechers;
    
    private final BotLeecherFactory botLeecherFactory;

    private final BotMediator mediator;

    
    /** Creates a new instance of Main */
    @Inject
    public IrcConnection(NicknameProvider nickProvider, BotLeecherFactory botLeecherFactory, BotMediator mediator) {
        super(new Configuration.Builder()
                .setLogin(nickProvider.getNickName()).setName(nickProvider.getNickName())
                .setFinger(nickProvider.getNickName()).setVersion("xxx").setAutoNickChange(true)
                .addListener(mediator).setServerHostname(mediator.getServer()).addAutoJoinChannel(mediator.getChannel())
                .buildConfiguration());
        this.botLeecherFactory = botLeecherFactory;
        
        this.leechers = Collections.synchronizedMap(new HashMap<String,BotLeecher>());
        this.listeners = new Vector<IrcConnectionListener>();
        this.listeners.add(mediator);
        this.mediator = mediator;

    }
    
    /**
     *
     *
     * @param user 
     * @return 
     */
    public BotLeecher makeLeecher(User user) {
        BotLeecher leecher = botLeecherFactory.getBotLeecher(user, this);
        leechers.put(user.getNick(),leecher);
        leecher.addListener(mediator);
        leecher.start();
        return leecher;
    }

    public void removeLeecher(User user) {
        final BotLeecher leecher = leechers.get(user.getNick());
        if (leecher != null) {
            leechers.remove(leecher);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        leechers.clear();
    }
    
    /**
     * 
     * @param botName 
     * @return 
     */
    public BotLeecher getBotLeecher(String botName){
        return leechers.get(botName);
    }

    public List<BotLeecher> getAllBots() {
        return new ArrayList<BotLeecher>(leechers.values());
    }
    
    /**
     *
     * @param listener
     */
    public void removeBotListener(IrcConnectionListener listener) {
        listeners.remove(listener);
    }


}
