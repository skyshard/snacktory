package de.jetwick.snacktory.models;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author Abhishek Mulay
 */
abstract public class BaseEntity {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
