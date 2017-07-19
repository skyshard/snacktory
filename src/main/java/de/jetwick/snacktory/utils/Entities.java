package de.jetwick.snacktory.utils;

import javax.swing.text.html.parser.Entity;
import java.util.List;

enum EntityType {
    PERSON("Person"), ORGANIZATION("Organization");

    String value;
    EntityType(String value) {
        this.value = value;
    }
}

public class Entities extends BaseEntity {

    List<NamedEntity> entities;

    public List<NamedEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<NamedEntity> entities) {
        this.entities = entities;
    }

}
