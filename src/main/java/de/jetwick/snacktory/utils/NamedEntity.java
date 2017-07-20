package de.jetwick.snacktory.utils;


/**
 * @author Abhishek Mulay
 */
public class NamedEntity extends BaseEntity {

    EntityType type;
    String representative;

    public NamedEntity() {
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }
}
