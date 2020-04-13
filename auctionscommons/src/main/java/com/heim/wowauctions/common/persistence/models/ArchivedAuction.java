package com.heim.wowauctions.common.persistence.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */

@Document(collection = "auctionsArchive")
@Data
public class ArchivedAuction {

    @JsonView(BaseView.class)
    private long auc;
    private long itemId;
    private String owner;
    private String ownerRealm;
    private long bid;
    private long buyout;
    private long ppi;
    private int quantity;
    private String timeLeft;
    @JsonView(BaseView.class)
    private int rand;
    @JsonView(BaseView.class)
    private long seed;
    private long timestamp;


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ auc: ").append(this.getAuc()).
                append(", owner: ").append(this.getOwner()).
                append("-").append(this.getOwnerRealm()).
                append(", bid: ").append(this.getBid()).
                append(", buyout: ").append(this.getBuyout()).
                append(", timeleft: ").append(this.getTimeLeft())
                .append("}");

        return sb.toString();
    }

    public interface BaseView {
    }
}
