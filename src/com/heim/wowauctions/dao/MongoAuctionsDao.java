package com.heim.wowauctions.dao;

import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 10:41 PM
 */

public class MongoAuctionsDao extends MongoTemplate {



    @Autowired
    public MongoAuctionsDao(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }

    public List<Auction> findAuctionsByItemId(long itemId) {

        List<Auction> qp = this.find(query(where("itemId").is(itemId)), Auction.class);
        return qp;
    }

    public void insertAllAuctions(List<Auction> auctions) {
        for (Auction auction : auctions) {
            this.insert(auction);
        }
    }

    public List<Auction> getRebornsAuctions(List<Long> ids, long timestamp) {
        Query q = new Query(where("item").in(ids).and("timestamp").is(timestamp));

        return this.find(q, Auction.class);
    }


    public List<Item> findItemByItemId(long id) {

        List<Item> qp = this.find(query(where("id").is(id)), Item.class);
        return qp;
    }


    public List<Item> findItemByName(String name) {
        Query q = new Query(Criteria.where("name").regex(name.toString(), "i"));
        List<Item> qp = this.find(q, Item.class);
        return qp;
    }

    //todo we will build up a queue from this list of ids to retrieve from external web service
    @SuppressWarnings("Unchecked")
    public List<Long> findAllAuctionItemIds(long timestamp) {

        List<Long> coll;
        if (timestamp == 0) {
            coll = this.getCollection("auction").distinct("item");
        } else {
            Map m = new HashMap();
            m.put("timestamp", timestamp);
            DBObject q = new BasicDBObject(m);
            coll = this.getCollection("auction").distinct("item", q);
        }

        return coll;
    }

    public void insertItem(Item item) {
        this.insert(item);
    }

    public void insertItems(List<Item> itemList) {
        for (Item item : itemList)
            this.insert(item);
    }


    public void insertAuctionsUrlData(AuctionUrl url) {
        this.insert(url);
    }

    public void updateAuctionsUrl(AuctionUrl url) {
        Query q = new Query();
        this.updateFirst(q, Update.update("lastModified", url.getLastModified()).set("url", url.getUrl()), AuctionUrl.class);
    }


    public AuctionUrl getAuctionsUrl() {
        Query query = new Query();
        return this.findOne(query, AuctionUrl.class);
    }

    public void removeAllFromAuctionUrls() {
        Query query1 = new Query();
        this.remove(query1, AuctionUrl.class);

    }

    public void removeAllFromAuctions() {
        Query query1 = new Query();
        this.remove(query1, Auction.class);

    }
}