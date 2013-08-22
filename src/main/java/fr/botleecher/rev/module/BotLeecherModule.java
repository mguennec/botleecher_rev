/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.botleecher.rev.module;

import com.google.inject.AbstractModule;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.service.*;

/**
 * Guice configuration module
 * @author fdb
 */
public class BotLeecherModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(Settings.class).to(SettingsImpl.class);
        bind(NicknameProvider.class).to(SettingsNicknameProvider.class);
        bind(BotLeecherFactory.class).to(BotLeecherFactoryImpl.class);
        bind(PackListReader.class).to(PackListReaderImpl.class);

        bind(IrcConnection.class).toProvider(ConnectionProvider.class);
    }

}
