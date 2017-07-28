package de.jetwick.snacktory.models;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

/**
 * @author Abhishek Mulay
 */
public class AuthorInfo {

    private String[] names;
    private EntityType entityType;

    public AuthorInfo() {
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(Collection<String> names) {
        this.names = names.toArray(new String[names.size()]);
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getNamesAsString() {
        return StringUtils.join(names, ", ");
    }
}
