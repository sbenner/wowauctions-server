package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.models.Realm;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */

//we keep disabled it for now
//@Component
public class AuctionMasterServerSyncService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuctionMasterServerSyncService.class);


    @Autowired
    HttpReqHandler httpReqHandler;
    @Autowired
    private MongoAuctionsDao auctionsDao;

    //  @Scheduled(fixedRate = 3600000)
    public void retrieveServerAuction() {
        logger.debug("started");
        try {

            Map<String, Integer> realmsMap = httpReqHandler.getServers();
            List<Realm> realmsList = getAuctionsDao().getAllRealms();

            for (Realm realm : realmsList) {
                if (realmsMap.get(realm.getName().toLowerCase()) == null) {
                    logger.info(" realm " + realm.getName());
                } else {
                    realm.setPopulation(realmsMap.get(realm.getName().toLowerCase()));
                    getAuctionsDao().updateRealm(realm);
                }
            }

            // List<Realm> aggregatedRealms = getAuctionsDao().aggregateRealms();
            //how work on this realm list q to download auctions
            //todo: rebuild our downloader to download from all realms.

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }

    }

    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
