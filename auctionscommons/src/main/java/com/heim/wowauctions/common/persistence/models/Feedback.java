package com.heim.wowauctions.common.persistence.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */

@Document
public class Feedback {

    private long timestamp;
    private String feedback;
    private String from;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
