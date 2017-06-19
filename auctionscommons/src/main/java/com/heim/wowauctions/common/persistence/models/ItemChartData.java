package com.heim.wowauctions.common.persistence.models;

import java.util.Map;

/**
 * Created by sbenner on 13/06/2017.
 */
public class ItemChartData {

    private long itemId;
    private Map<Long,Long> valueTime;
    private int quantity;
    private long timestamp;


    public Map<Long, Long> getValueTime() {
        return valueTime;
    }

    public void setValueTime(Map<Long, Long> valueTime) {
        this.valueTime = valueTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
