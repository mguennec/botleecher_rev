/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.service;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * @author fdb
 */
@Singleton
public class SettingsImpl implements Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsImpl.class);
    private static final String CONFIG_FILE_NAME = "botleecher.properties";
    private static final String PROP_SAVEFOLDER = "savefolder";
    private static final String PROP_SERVER = "servers";
    private static final String PROP_CHANNEL = "channels";
    private static final String PROP_NICKS = "nicks";
    private static final String PROP_KEYWORDS = "keywords";
    private static final String SEPARATOR = ",";
    private Properties configFile = loadConfig();

    @Override
    public File getSaveFolder() {
        File saveFolder = null;
        String folder = configFile.getProperty(PROP_SAVEFOLDER);
        if (folder != null) {
            saveFolder = new File(folder);
        } else {
            File userHome = new File(System.getProperty("user.home"));
            File newFolder = new File(userHome, "downloads");
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            configFile.setProperty(PROP_SAVEFOLDER, newFolder.getAbsolutePath());
            saveConfig(configFile);
        }
        return saveFolder;
    }

    @Override
    public void setSaveFolder(String dir) {
        configFile.setProperty(PROP_SAVEFOLDER, dir);
        saveConfig(configFile);
    }

    @Override
    public List<String> getServers() {
        return getList(PROP_SERVER, "irc.rizon.net");
    }

    @Override
    public void addServer(String server) {
        add(PROP_SERVER, server);
    }

    @Override
    public List<String> getChannels() {
        return getList(PROP_CHANNEL, "");
    }

    @Override
    public void addChannel(String channel) {
        add(PROP_CHANNEL, channel);
    }

    @Override
    public List<String> getNicks() {
        return getList(PROP_NICKS, "");
    }

    @Override
    public void setNicks(String nicks) {
        configFile.setProperty(PROP_NICKS, nicks);
        saveConfig(configFile);
    }

    @Override
    public List<String> getKeywords() {
        return getList(PROP_KEYWORDS, "");
    }

    @Override
    public void setKeywords(List<String> keywords) {
        final StringBuilder builder = new StringBuilder();
        if (keywords != null) {
            for (String keyword : keywords) {
                if (builder.length() > 0) {
                    builder.append(SEPARATOR);
                }
                builder.append(keyword);
            }
            setKeywords(builder.toString());
        }
    }

    @Override
    public void setKeywords(String keywords) {
        configFile.setProperty(PROP_KEYWORDS, keywords);
        saveConfig(configFile);
    }

    @Override
    public void addNick(String nick) {
        add(PROP_NICKS, nick);
    }

    private void add(String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            final String property = configFile.getProperty(name);
            if (StringUtils.isBlank(property)) {
                configFile.setProperty(name, value);
            } else {
                configFile.setProperty(name, SEPARATOR + value);
            }
            saveConfig(configFile);
        }

    }

    private List<String> getList(String name, String init) {
        final String property = configFile.getProperty(name);
        final List<String> list;
        if (StringUtils.isBlank(property)) {
            list = new ArrayList<>(Arrays.asList(init));
            add(name, init);
        } else {
            final String[] split = StringUtils.split(property, SEPARATOR);
            list = new ArrayList<>(Arrays.asList(split));
        }
        return list;
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
        saveConfig(configFile);
    }

    private void saveConfig(Properties configFile) {
        try (FileWriter writer = new FileWriter(getConfigFilePath())) {
            configFile.store(writer, "botleecher configuration file");
        } catch (IOException ex) {
            LOGGER.error("Error writing properties to file", ex);
        }
    }
}
