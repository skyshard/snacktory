package de.jetwick.snacktory.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Abhishek Mulay
 */
public enum EntityType {
    PERSON,
    ORGANIZATION;

    private static Map<String, EntityType> entityMap = new HashMap<>(EntityType.values().length);

    static {
        for (EntityType entityType : EntityType.values()) {
            entityMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    @JsonCreator
    public static EntityType forValue(String value) {
        return entityMap.get(StringUtils.lowerCase(value));
    }
}
