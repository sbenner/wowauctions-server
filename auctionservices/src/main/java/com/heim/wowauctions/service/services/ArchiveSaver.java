package com.heim.wowauctions.service.services;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.models.Auction;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */


public class ArchiveSaver implements Runnable {

    private final Auction auction;
    private final MongoAuctionsDao mongoAuctionsDao;
    private final AuctionsSyncService service;

    public ArchiveSaver(AuctionsSyncService service, Auction auction, MongoAuctionsDao mongoAuctionsDao) {
        this.mongoAuctionsDao = mongoAuctionsDao;
        this.service = service;
        this.auction = auction;
    }

    public void run() {
        processItem();
    }

    private void processItem() {
        mongoAuctionsDao.archiveAuctions(auction);
        service.getSemaphore().release();
    }

}