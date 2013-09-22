/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.botleecher.rev.service.mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.botleecher.rev.entities.Setting;
import fr.botleecher.rev.enums.SettingProperty;
import fr.botleecher.rev.service.Settings;


/**
 * @author fdb
 */
@Singleton
public class MongoSettingsImpl implements Settings {

    @Inject
    private MongoConnector connector;

    @Override
    public Setting get(final SettingProperty property) throws Exception {
        Setting setting = connector.getOne(new Setting(property));
        if (setting == null) {
            setting = getDefaultValue(property);
            connector.update(setting);
        }
        return setting;
    }

    @Override
    public void save(final Setting setting) throws Exception {
        if (setting.getId() == null) {
            final Setting dbSetting = get(setting.getKey());
            if (dbSetting != null) {
                setting.setId(dbSetting.getId());
            }
        }
        connector.update(setting);
    }

    private Setting getDefaultValue(final SettingProperty property) {
        return new Setting(property, property.getDefaultValue());
    }


}
