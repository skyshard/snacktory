package de.jetwick.snacktory.utils;


import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Abhishek Mulay
 */
public class NamedEntity extends BaseEntity {

    EntityType type;
    String representative;

    @JsonProperty("salience_score")
    Double salienceScore;

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

    public Double getSalienceScore() {
        return salienceScore;
    }

    public void setSalienceScore(Double salienceScore) {
        this.salienceScore = salienceScore;
    }
}
