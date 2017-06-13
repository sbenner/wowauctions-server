package com.heim.wowauctions.service.services;

import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */


public class ArchiveSaver implements Runnable {

//    private static final Logger logger = LoggerFactory.getLogger(ArchiveSaver.class);


    private Auction auction;
    private MongoAuctionsDao mongoAuctionsDao;
    private AuctionsSyncService service;

    public ArchiveSaver(AuctionsSyncService service, Auction auction) {

        setMongoAuctionsDao(service.getAuctionsDao());
        setService(service);
        setAuction(auction);
    }

    public void run() {
        processItem();

    }

    private void processItem() {
        mongoAuctionsDao.archiveAuctions(getAuction());
        getService().getSemaphore().release();
    }

    private MongoAuctionsDao getMongoAuctionsDao() {
        return mongoAuctionsDao;
    }

    private void setMongoAuctionsDao(MongoAuctionsDao mongoAuctionsDao) {
        this.mongoAuctionsDao = mongoAuctionsDao;
    }

    public AuctionsSyncService getService() {
        return service;
    }

    public void setService(AuctionsSyncService service) {
        this.service = service;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}