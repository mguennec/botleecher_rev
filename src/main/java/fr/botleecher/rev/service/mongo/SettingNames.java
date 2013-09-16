package fr.botleecher.rev.service.mongo;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 16/09/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public enum SettingNames {
    PROP_SAVEFOLDER("savefolder"), PROP_SERVER("servers"), PROP_CHANNEL("channels"),PROP_NICKS("nicks"),PROP_KEYWORDS("keywords");

    private final String propertyName;

    private SettingNames(final String name) {
        this.propertyName = name;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static SettingNames getByPropertyName(final String name) {
        SettingNames settingNames = null;
        for (SettingNames setting : values()) {
            if (StringUtils.equals(name, setting.getPropertyName())) {
                settingNames = setting;
                break;
            }
        }
        return settingNames;
    }
}
