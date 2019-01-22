package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.SolrAuctionsService;
import com.heim.wowauctions.common.persistence.models.Auction;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */


public class ArchiveSaver implements Runnable {

//    private static final Logger logger = LoggerFactory.getLogger(ArchiveSaver.class);


    private Auction auction;

    private AuctionsSyncService service;

    SolrAuctionsService auctionsService;

    public ArchiveSaver(AuctionsSyncService service, Auction auction, SolrAuctionsService auctionsService) {
        this.auctionsService = auctionsService;
        this.service=service;
        this.auction=auction;
    }

    public void run() {
        processItem();

    }

    private void processItem() {
        auctionsService.saveAuction(auction);
        service.getSemaphore().release();
    }

}