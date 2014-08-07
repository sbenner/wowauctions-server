package com.heim.wowauctions.services;


import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import com.heim.wowauctions.utils.AuctionUtils;
import com.heim.wowauctions.utils.NetUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync non existent items with the local db
 */



public class ItemsSyncService extends TimerTask {

    protected static long		ONE_DAY	= 24 * 3600 * 1000L;
    private static final Logger logger	= Logger.getLogger(ItemsSyncService.class.getSimpleName());
    private static String url = "http://us.battle.net/api/wow/auction/data/veknilash";

    private MongoAuctionsDao auctionsTemplate;

    public void run()
    {
           logger.info("started");
           try{




          }catch(Exception e)
               {e.printStackTrace();}


    }





    public MongoAuctionsDao getAuctionsTemplate() {
        return auctionsTemplate;
    }

    public void setAuctionsTemplate(MongoAuctionsDao auctionsTemplate) {
        this.auctionsTemplate = auctionsTemplate;
    }
}
