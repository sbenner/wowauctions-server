package com.heim.wowauctions.service.services;


import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.persistence.models.Realm;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */

@Component
public class AuctionMasterServerSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(AuctionMasterServerSyncService.class);
    @Autowired
    HttpReqHandler httpReqHandler;
    @Autowired
    private MongoAuctionsDao auctionsDao;

    public void run() {
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

            List<Realm> aggregatedRealms = getAuctionsDao().aggregateRealms();
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
