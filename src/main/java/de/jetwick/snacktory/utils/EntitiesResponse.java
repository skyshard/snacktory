package de.jetwick.snacktory.utils;

import java.util.List;

/**
 * Model class represents a response received from /entities Api
 *
 * @author Abhishek Mulay
 */
public class EntitiesResponse extends BaseEntity {

    List<NamedEntity> entities;

    public List<NamedEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<NamedEntity> entities) {
        this.entities = entities;
    }

}
