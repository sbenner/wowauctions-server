package com.heim.wowauctions.common.persistence.dao;

import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.persistence.models.Realm;
import com.mongodb.client.model.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 10:41 PM
 */
@Component
public class MongoAuctionsDao extends MongoTemplate {

    private static final Logger logger = LoggerFactory.getLogger(MongoAuctionsDao.class);

    @Autowired
    public MongoAuctionsDao(MongoDbFactory mongoDbFactory) {
        super(mongoDbFactory);
    }


//    @Autowired
//    public MongoAuctionsDao(Mongo mongo,String databaseName) {
//        super(mongo,databaseName);
//    }


    //todo we will build up a queue from this list of ids to retrieve from external web service
    @SuppressWarnings("Unchecked")
    public List<Long> findAllAuctionItemIds(long timestamp) {

        List<Long> coll = new ArrayList<>();

        if (timestamp == 0) {
            this.getCollection("auction").distinct("itemId", Long.class)
                    .iterator().forEachRemaining(coll::add);
        } else {
//            Map<String, Long> m = new HashMap<>();
//            m.put("timestamp", timestamp);
//            DBObject q = new BasicDBObject(m);

            this.getCollection("auction")
                    .distinct("itemId", Filters.eq("timestamp", timestamp), Long.class)
                    .iterator().forEachRemaining(coll::add);
        }

        return coll;
    }


    public void updateRealm(Realm realm) {
        Query q = new Query(where("slug").is(realm.getSlug()));
        Update u = Update.update("population", realm.getPopulation());

        this.updateFirst(q, u, Realm.class);
    }


    public Set<Long> getAllItemIDs() {
        Set<Long> existingItemsSet = new HashSet<Long>();
        this.getCollection("item").distinct("itemId", Long.class)
                .iterator().forEachRemaining(existingItemsSet::add);
        return existingItemsSet;

    }

    public List<Realm> aggregateRealms() {

        TypedAggregation agg = Aggregation.newAggregation(Realm.class,
                Aggregation.group("connected").
                        max("population").as("population").
                        max("connected").as("connected").
                        max("slug").as("slug")
        );
        AggregationResults<Realm> results = this.aggregate(agg, Realm.class);

        List<Realm> returnedList = findNotConnectedRealms();
        returnedList.addAll(results.getMappedResults());

        return returnedList;
    }

    private List<Realm> findNotConnectedRealms() {

        Query q = new Query(where("connected").exists(false));

        return this.find(q, Realm.class);

    }


    public List<Auction> findAuctionsToArchive(long timestamp) {

        Query q = new Query(where("timestamp").lt(timestamp));
        return this.find(q, Auction.class);
    }


    public void archiveAuctions(Auction auction) {
        try {
            logger.info("saving " + auction.toString());
            this.save(auction, "auctionsArchive");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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
        this.updateFirst(q, Update.update("lastModified", url.getLastModified())
                .set("url", url.getUrl()), AuctionUrl.class);
    }


    public AuctionUrl getAuctionsUrl() {
        Query query = new Query();

        return this.findOne(query, AuctionUrl.class);
    }
}