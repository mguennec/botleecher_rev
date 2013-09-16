/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.service.mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.botleecher.rev.service.Settings;
import fr.botleecher.rev.service.mongo.entities.Setting;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * @author fdb
 */
@Singleton
public class MongoSettingsImpl implements Settings {

    @Inject
    private MongoConnector connector;

    private Setting getObject(final SettingNames property) throws Exception {
        Setting object = connector.getOne(new Setting(property.getPropertyName()));
        if (object == null) {
            object = getDefaultValue(property);
            updateObject(object);
        }
        return object;
    }

    private void updateObject(final Setting setting) throws Exception {
        connector.update(setting);
    }

    private Setting getDefaultValue(final SettingNames property) {
        final List<String> value;
        // TODO Change default value before pushing
        switch (property) {
            case PROP_SAVEFOLDER:
                value = new ArrayList<>(Arrays.asList(new File(new File(System.getProperty("user.home")), "downloads").getAbsolutePath()));
                break;
            case PROP_SERVER:
                value = new ArrayList<>(Arrays.asList("irc.rizon.net"));
                break;
            case PROP_CHANNEL:
                value = new ArrayList<>(Arrays.asList(""));
                break;
            case PROP_NICKS:
                value = new ArrayList<>(Arrays.asList("leecher", "noname", "nonameleecher"));
                break;
            case PROP_KEYWORDS:
                value = new ArrayList<>(Arrays.asList("added"));
                break;
            default:
                value = null;
                break;
        }
        return new Setting(property.getPropertyName(), value);
    }

    private Setting getObject(final SettingNames property, final List<String> value) throws Exception {
        final Setting setting = getObject(property);
        setting.setValue(value);
        return setting;
    }

    @Override
    public String getSaveFolder() throws Exception {
        final List<String> object = getObject(SettingNames.PROP_SAVEFOLDER).getValue();
        final String file;
        if (object != null && !object.isEmpty()) {
            file = object.get(0);
        } else {
            file = null;
        }
        return file;
    }

    @Override
    public void setSaveFolder(String dir) throws Exception {
        updateObject(getObject(SettingNames.PROP_SAVEFOLDER, new ArrayList<>(Arrays.asList(dir))));
    }

    @Override
    public List<String> getServers() throws Exception {
        return getList(SettingNames.PROP_SERVER);
    }

    @Override
    public void addServer(String server) throws Exception {
        addToList(SettingNames.PROP_SERVER, server);
    }

    @Override
    public List<String> getChannels() throws Exception {
        return getList(SettingNames.PROP_CHANNEL);
    }

    @Override
    public void addChannel(String channel) throws Exception {
        addToList(SettingNames.PROP_CHANNEL, channel);
    }

    @Override
    public List<String> getNicks() throws Exception {
        return getList(SettingNames.PROP_NICKS);
    }

    @Override
    public void setNicks(List<String> nicks) throws Exception {
        updateObject(getObject(SettingNames.PROP_NICKS, new ArrayList<>(nicks)));
    }

    @Override
    public List<String> getKeywords() throws Exception {
        return getList(SettingNames.PROP_KEYWORDS);
    }

    @Override
    public void setKeywords(List<String> keywords) throws Exception {
        updateObject(getObject(SettingNames.PROP_KEYWORDS, new ArrayList<>(keywords)));
    }

    @Override
    public void addNick(String nick) throws Exception {
        addToList(SettingNames.PROP_NICKS, nick);
    }

    private void addToList(final SettingNames property, final String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            return;
        }
        final Setting object = getObject(property);
        final Object list = object.getValue();
        if (list instanceof Collection) {
            if (((Collection) list).contains(value)) {
                ((Collection<String>) list).add(value);
                updateObject(object);
            }
        }
    }

    private List<String> getList(final SettingNames property) throws Exception {
        final Object object = getObject(property).getValue();
        final List<String> values = new ArrayList<>();
        if (object instanceof Collection) {
            values.addAll((Collection<String>) object);
        }
        return values;
    }

}
