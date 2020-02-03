package com.heim.wowauctions.common.persistence.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/5/14
 * Time: 1:01 AM
 */
public class Item implements Persistable {

    @Id
    private ObjectId id;

    private long itemId;
    private String name;
    private int itemLevel;
    private int quality;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(int itemLevel) {
        this.itemLevel = itemLevel;
    }


    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return (getId() == null);
    }
}


