package fr.botleecher.rev.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.service.BotLeecherFactory;
import fr.botleecher.rev.service.BotMediator;
import fr.botleecher.rev.service.NicknameProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 08/07/13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionProvider implements Provider<IrcConnection> {
    private NicknameProvider nickProvider;
    private BotLeecherFactory botLeecherFactory;
    private BotMediator mediator;

    @Inject
    public ConnectionProvider(NicknameProvider nicknameProvider, BotLeecherFactory botLeecherFactory, BotMediator botMediator) {
        this.nickProvider = nicknameProvider;
        this.botLeecherFactory = botLeecherFactory;
        this.mediator = botMediator;
    }

    @Override
    public IrcConnection get() {
        try {
            return new IrcConnection(nickProvider, botLeecherFactory, mediator);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
