package de.jetwick.snacktory.utils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Created by admin- on 19/7/17.
 */
abstract public class BaseEntity {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
