package fr.botleecher.rev.enums;

import com.github.jmkgreen.morphia.annotations.Converters;
import com.github.jmkgreen.morphia.converters.EnumConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 16/09/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
@Converters(EnumConverter.class)
public enum SettingProperty {
    PROP_SAVEFOLDER("savefolder", "X:\\Anime"),
    PROP_SERVER("servers", "irc.rizon.net"),
    PROP_CHANNEL("channels", "#exiled-destiny"),
    PROP_NICKS("nicks", "namekman", "namekmin", "namekman22"),
    PROP_KEYWORDS("keywords", "added"),
    PROP_STORAGETYPE("db.type", StorageType.FILES.getType()),
    PROP_STORAGEPATH("db.path", "F:/botleecher/db");

    private final String propertyName;
    private final List<String> defaultValue;

    private SettingProperty(final String name, final String... defaultValue) {
        this.propertyName = name;
        this.defaultValue = Arrays.asList(defaultValue);
    }

    public static SettingProperty getByPropertyName(final String name) {
        SettingProperty settingNames = null;
        for (SettingProperty setting : values()) {
            if (StringUtils.equals(name, setting.getPropertyName())) {
                settingNames = setting;
                break;
            }
        }
        return settingNames;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public List<String> getDefaultValue() {
        return defaultValue;
    }
}
