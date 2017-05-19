package com.heim.wowauctions.service.services;


import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.persistence.models.Auction;
import com.heim.wowauctions.service.persistence.models.AuctionUrl;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */

@Component
public class AuctionsSyncService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionsSyncService.class);
    private static final String url = "https://us.api.battle.net/wow/auction/data/veknilash";

    private final MongoAuctionsDao auctionsDao;
    private final HttpReqHandler httpReqHandler;

    @Autowired
    public AuctionsSyncService(MongoAuctionsDao auctionsDao, HttpReqHandler httpReqHandler) {
        this.auctionsDao = auctionsDao;
        this.httpReqHandler = httpReqHandler;
    }

    @Scheduled(fixedRate = 180000)
    public synchronized void retrieveAuctions() {
        logger.debug("started");
        try {

            String out =
                    httpReqHandler.getData(url);

            AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);

            logger.info("local "+local.toString());
            logger.info("remote "+remote.toString());

            if (local == null ||
                    local.getLastModified() < remote.getLastModified() ||
                    getAuctionsDao().getAuctionsCount() == 0) {

                logger.info(format("local.getLastModified() %s", local.getLastModified()));

                logger.info(format("remote.getLastModified() %s", remote.getLastModified()));
                logger.info(format("local.getLastModified() < remote.getLastModified() %s", local.getLastModified() < remote.getLastModified()));
                logger.info(format("getAuctionsDao().getAuctionsCount() %s", getAuctionsDao().getAuctionsCount()));

                //get new auctions
                String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (auctionsString != null) {
                    List<Auction> auctions = AuctionUtils.buildAuctionsFromString(auctionsString, remote.getLastModified());
                    getAuctionsDao().insertAll(auctions);

                    //archive old
                    List<Auction> toArchive = getAuctionsDao().findAuctionsToArchive(remote.getLastModified());
                    if(getAuctionsDao().archiveAuctions(toArchive)) {
                        getAuctionsDao().removeArchivedAuctions(remote.getLastModified());
                    }

                    if (local.getUrl() == null) {
                        getAuctionsDao().insertAuctionsUrlData(remote);
                    } else {
                        getAuctionsDao().updateAuctionsUrl(remote);
                    }

                }

            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

}
