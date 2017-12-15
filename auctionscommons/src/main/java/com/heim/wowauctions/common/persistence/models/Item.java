package com.heim.wowauctions.common.persistence.models;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/5/14
 * Time: 1:01 AM
 */
public class Item {

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
}


