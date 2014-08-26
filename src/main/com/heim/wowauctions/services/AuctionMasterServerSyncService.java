package com.heim.wowauctions.services;


import com.heim.wowauctions.dao.MongoAuctionsDao;

import com.heim.wowauctions.models.Realm;
import com.heim.wowauctions.utils.NetUtils;
import org.apache.log4j.Logger;

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


public class AuctionMasterServerSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(AuctionMasterServerSyncService.class);

    private MongoAuctionsDao auctionsDao;


    public void run() {
        logger.debug("started");
        try {

            Map<String, Integer> realmsMap = NetUtils.getServers();
            List<Realm> realmsList = getAuctionsDao().getAllRealms();

            for(Realm realm : realmsList){
                      if(realmsMap.get(realm.getName().toLowerCase())==null)
                      {
                          System.out.println("Oh Snap!!!!"+realm.getName());
                      }
                      else{
                      realm.setPopulation(realmsMap.get(realm.getName().toLowerCase()));
                      getAuctionsDao().updateRealm(realm);
                      }
            }

            List<Realm> aggregatedRealms = getAuctionsDao().aggregateRealms();
               //how work on this realm list q to download auctions
            //todo: rebuild our downloader to download from all realms.

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
