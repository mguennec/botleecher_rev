package fr.botleecher.rev.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 17/09/13
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public enum StorageType {
    FILES("files"), EMBEDDED_MONGO("embedded_mongo");

    private final String type;

    private StorageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static StorageType getByType(final String type) {
        StorageType storageType = null;
        for (StorageType value : values()) {
            if (StringUtils.equals(type, value.getType())) {
                storageType = value;
                break;
            }
        }
        return storageType;
    }
}
