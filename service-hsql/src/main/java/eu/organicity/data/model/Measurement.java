package eu.organicity.data.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String assetUrn;

    private String attribute;

    private long timestamp;

    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetUrn() {
        return assetUrn;
    }

    public void setAssetUrn(String assetUrn) {
        this.assetUrn = assetUrn;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
