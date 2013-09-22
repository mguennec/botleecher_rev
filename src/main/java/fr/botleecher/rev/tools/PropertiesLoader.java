package fr.botleecher.rev.tools;

import fr.botleecher.rev.service.properties.SettingsImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 17/09/13
 * Time: 09:53
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsImpl.class);
    private static final String CONFIG_FILE_NAME = "botleecher.properties";
    private static PropertiesLoader instance;
    private Properties configFile = loadConfig();

    public static PropertiesLoader getInstance() {
        if (instance == null) {
            synchronized (PropertiesLoader.class) {
                if (instance == null) {
                    instance = new PropertiesLoader();
                }
            }
        }
        return instance;
    }

    private PropertiesLoader() {
        // Nothing
    }


    private String getConfigFilePath() {
        String path = System.getProperty("user.home");
        path += File.separator + CONFIG_FILE_NAME;
        return path;
    }
    private Properties loadConfig() {
        final Properties configFile = new Properties();
        try (FileInputStream fis = new FileInputStream(getConfigFilePath())) {
            configFile.load(fis);
        } catch (IOException ex) {
            LOGGER.error("properties file not found, generating new one", ex);
            createConfig();
        }

        return configFile;
    }

    private void createConfig() {
        configFile = new Properties();
        saveConfig();
    }

    public Properties getConfig() {
        return configFile;
    }

    public String getProperty(final String property, final String defaultValue) {
        String value = configFile.getProperty(property);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
            configFile.setProperty(property, defaultValue);
            saveConfig();
        }

        return value;
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(getConfigFilePath())) {
            configFile.store(writer, "botleecher configuration file");
        } catch (IOException ex) {
            LOGGER.error("Error writing properties to file", ex);
        }
    }
}
