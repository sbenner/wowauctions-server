package com.heim.wowauctions.common.persistence.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */

@Data
@SolrDocument(collection = "auctions")
public class Auction implements Comparable<Auction> {


    //String id;
    @JsonView(ArchivedAuction.BaseView.class)
    @Id
    String id;

    @Indexed(name = "auc_l")
    private long auc;
    @Indexed(name = "item_id_l")
    private long itemId;
    @Field("owner_s")
    private String owner;
    @Field("owner_realm_s")
    private String ownerRealm;
    @Indexed(name = "bid_l")
    private long bid;
    @Indexed(name = "buyout_l")
    private long buyout;
    @Indexed(name = "qty_d")
    private int quantity;
    @Indexed(name = "time_left_s")
    private String timeLeft;
    @JsonView(ArchivedAuction.BaseView.class)
    @Indexed(name = "rand_d")
    private int rand;
    @JsonView(ArchivedAuction.BaseView.class)
    @Indexed(name = "seed_l")
    private long seed;
    @Indexed(name = "timestamp_l")
    private long timestamp;
    @Indexed(name = "date_d")
    private Date date;
    @Indexed(name = "ppi_l")
    private BigDecimal ppi;

    public int compareTo(Auction compareAuction) {

        BigDecimal compare = compareAuction.ppi;
        //ascending order
        //return this.quantity - compareQuantity;
        return this.ppi.compareTo(compare);
        //return (int) (this.ppi - compare);

        //descending order
        //return (int)(compare - this.ppi);

    }

    public interface BaseView {
    }


}
