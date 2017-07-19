package de.jetwick.snacktory.utils;

/**
 * Created by admin- on 19/7/17.
 */




public class NamedEntity extends BaseEntity {

    String type;
    String representative;

    public NamedEntity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }
}
