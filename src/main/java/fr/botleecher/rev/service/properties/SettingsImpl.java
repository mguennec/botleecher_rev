/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.service.properties;

import com.google.inject.Singleton;
import fr.botleecher.rev.entities.Setting;
import fr.botleecher.rev.enums.SettingProperty;
import fr.botleecher.rev.service.Settings;
import fr.botleecher.rev.tools.PropertiesLoader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * @author fdb
 */
@Singleton
public class SettingsImpl implements Settings {

    private static final String SEPARATOR = ",";
    private PropertiesLoader loader = PropertiesLoader.getInstance();
    private Properties configFile = loader.getConfig();

    private String getPropertyFromList(final List<String> list) {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                builder.append(SEPARATOR);
            }
            builder.append(string);
        }
        return builder.toString();
    }

    @Override
    public Setting get(SettingProperty property) throws Exception {
        final Setting setting = new Setting(property);
        final String value = configFile.getProperty(property.getPropertyName());
        if (StringUtils.isBlank(value)) {
            setting.setValue(property.getDefaultValue());
            save(setting);
        } else {
            final String[] split = StringUtils.split(value, SEPARATOR);
            setting.setValue(new ArrayList<>(Arrays.asList(split)));
        }
        return setting;
    }

    @Override
    public void save(Setting setting) throws Exception {
        configFile.setProperty(setting.getKey().getPropertyName(), getPropertyFromList(setting.getValue()));
        loader.saveConfig();
    }
}
