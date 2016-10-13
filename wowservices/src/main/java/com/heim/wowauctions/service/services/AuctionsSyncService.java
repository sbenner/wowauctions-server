package com.heim.wowauctions.service.services;


import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.persistence.models.Auction;
import com.heim.wowauctions.service.persistence.models.AuctionUrl;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimerTask;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */

@Component
public class AuctionsSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(AuctionsSyncService.class);
    private static final String url = "https://us.api.battle.net/wow/auction/data/veknilash";
    @Autowired
    private MongoAuctionsDao auctionsDao;
    @Autowired
    private HttpReqHandler httpReqHandler;

    public void run() {
        logger.debug("started");
        try {

            String out =
                    httpReqHandler.getData(url);


            AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);


            if (local == null ||
                    local.getLastModified() < remote.getLastModified() ||
                    getAuctionsDao().findAll(Auction.class).size() == 0) {

                //get new auctions
                String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (auctionsString != null) {
                    List<Auction> auctions = AuctionUtils.buildAuctionsFromString(auctionsString, remote.getLastModified());
                    getAuctionsDao().insertAll(auctions);

                    //archive old
                    List<Auction> toArchive = getAuctionsDao().findAuctionsToArchive(remote.getLastModified());
                    getAuctionsDao().archiveAuctions(toArchive);
                    getAuctionsDao().removeArchivedAuctions(remote.getLastModified());


                    if (local == null) {
                        getAuctionsDao().insertAuctionsUrlData(remote);
                    } else {
                        getAuctionsDao().updateAuctionsUrl(remote);
                    }

                }

            }


        } catch (Exception e) {
           logger.error(e.getMessage(),e);
        }

    }

    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
