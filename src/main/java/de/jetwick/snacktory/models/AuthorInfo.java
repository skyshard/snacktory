package de.jetwick.snacktory.models;

/**
 * @author Abhishek Mulay
 */
public class AuthorInfo {

    private String[] names;
    private EntityType entityType;

    public String[] getNames() {
        return names;
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
}
