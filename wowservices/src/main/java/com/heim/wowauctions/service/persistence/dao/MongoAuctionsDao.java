package com.heim.wowauctions.service.persistence.dao;

import com.heim.wowauctions.service.persistence.models.*;
import com.heim.wowauctions.service.persistence.repositories.*;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

@Component
public class MongoAuctionsDao extends MongoTemplate {

    @Autowired
    RealmRepository realmRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    ArchivedAuctionRepository archivedAuctionRepository;


    @Autowired
    public MongoAuctionsDao(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }


    public Page<Auction> getAuctionsByItemIDs(List<Long> ids, Pageable pageable) {
        return auctionRepository.findByItemIdIn(ids, pageable);
    }

    //todo: build aucitions with timestamp and ownerRealm if we go beyond 3 realms


    public List<Item> findItemByName(String name) {
        return itemRepository.findItemsByNameRegex(name);
    }


    public List<Item> findItemByExactName(String name) {
        return itemRepository.findItemsByNameRegexExactMatch(name);
    }

    //todo we will build up a queue from this list of ids to retrieve from external web service
    @SuppressWarnings("Unchecked")
    public List findAllAuctionItemIds(long timestamp) {

        List coll;
        if (timestamp == 0) {
            coll = this.getCollection("auction").distinct("itemId");
        } else {
            Map<String,Long> m = new HashMap<>();
            m.put("timestamp", timestamp);
            DBObject q = new BasicDBObject(m);
            coll = this.getCollection("auction").distinct("itemId", q);
        }

        return coll;
    }



    public List<Realm> getAllRealms(){

        return (ArrayList<Realm>)realmRepository.findAll();

    }


    public void updateRealm(Realm realm) {
        Query q = new Query(where("slug").is(realm.getSlug()));
        Update u =  Update.update("population",realm.getPopulation());

        this.updateFirst(q, u, Realm.class);
    }


    public List<ArchivedAuction> getItemStatisticsByTimestamp(long itemId) {

        return archivedAuctionRepository.findByTimestampBetween(AuctionUtils.getTimestamp(true), AuctionUtils.getTimestamp(false), itemId);

    }


    public List<ArchivedAuction> getItemStatistics(long itemId) {

        return archivedAuctionRepository.findByItemId(itemId);

    }

    public List<Long> getAllItemIDs() {
        return this.getCollection("item").distinct("itemId");

    }

    public List<Realm> aggregateRealms(){

        TypedAggregation agg = Aggregation.newAggregation(Realm.class,
                Aggregation.group("connected").
                        max("population").as("population").
                        max("connected").as("connected").
                        max("slug").as("slug")
        );
        AggregationResults<Realm> results =  this.aggregate(agg,Realm.class);

         List<Realm> returnedList = findNotConnectedRealms();
         returnedList.addAll(results.getMappedResults());

        return returnedList;
    }

    private List<Realm> findNotConnectedRealms(){

        Query q = new Query(where("connected").exists(false));

        return this.find(q,Realm.class);

    }


    public List<Auction> findAuctionsToArchive(long timestamp) {
        return this.find(query(where("timestamp").lt(timestamp)), Auction.class);
    }


    public void archiveAuctions(List<Auction> toArchiveList) {

        for (Auction auction : toArchiveList)
            this.insert(auction, "auctionsArchive");
    }

    public void removeArchivedAuctions(long timestamp) {
        Query query1 = new Query(where("timestamp").lt(timestamp));
        this.remove(query1, Auction.class);
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
}