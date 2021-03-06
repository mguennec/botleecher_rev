package fr.botleecher.rev.service.mongo;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import fr.botleecher.rev.enums.SettingProperty;
import fr.botleecher.rev.tools.PropertiesLoader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 14/09/13
 * Time: 01:53
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class Connection {

    private MongodExecutable executable;

    private MongoClient client;

    private PropertiesLoader loader = PropertiesLoader.getInstance();

    public Connection() throws Exception {
        createDb();
    }

    public MongoClient getConnection() throws Exception {
        if (client == null) {
            synchronized (this) {
                createDb();
            }
        }
        return client;
    }

    public void createDb() throws Exception {
        if (client != null) {
            return;
        }

        final String path = loader.getProperty(SettingProperty.PROP_STORAGEPATH.getPropertyName(), SettingProperty.PROP_STORAGEPATH.getDefaultValue().get(0));

        final int port = Network.getFreeServerPort();
        final IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).commandLinePostProcessor(new ICommandLinePostProcessor() {
            @Override
            public List<String> process(Distribution distribution, List<String> args) {
                final int i = args.indexOf("--dbpath");
                args.remove(i + 1);
                args.remove(i);
                args.add("--dbpath");
                args.add(path);
                return args;
            }
        })
                .artifactStore(new ArtifactStoreBuilder().defaults(Command.MongoD)
                        .download(new DownloadConfigBuilder().defaultsForCommand(Command.MongoD))
                        //        .executableNaming(new UserTempNaming())
                ).build();
        final IMongodConfig config = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                //.replication(new Storage(path, null, 0))
                .build();
        final MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

        executable = starter.prepare(config);
        executable.start();
        client = new MongoClient("localhost", port);
    }
}
