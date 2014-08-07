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
import java.util.*;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */



public class AuctionsSyncService extends TimerTask {

    protected static long		ONE_DAY	= 24 * 3600 * 1000L;
    private static final Logger logger	= Logger.getLogger(AuctionsSyncService.class.getSimpleName());
    private static String url = "http://us.battle.net/api/wow/auction/data/veknilash";
 //   private TransitionManager manager;
//    Date date = new Date( System.currentTimeMillis() - ONE_DAY );
//    Properties props;
//    SimpleDateFormat ts= new SimpleDateFormat("yyyy-MM-dd");
    private MongoAuctionsDao auctionsTemplate;
    //constructor


    
    public void run()
    {
           logger.info("task started");
           try{

               String out = // NetUtils.getResourceFromUrl("http://us.battle.net/api/wow/character/veknilash/heimdallur?fields=items");
                       NetUtils.getResourceFromUrl(url);

               if(auctionsTemplate.find(new Query(),Item.class).size()==0){
                List<Item> items = AuctionUtils.makeReborns();
                auctionsTemplate.insertItems(items);

               }

               AuctionUrl local = auctionsTemplate.getAuctionsUrl();
               AuctionUrl remote = AuctionUtils.parseAuctionFile(out);


               if(local==null||
                       local.getLastModified()<remote.getLastModified()||
                       auctionsTemplate.findAll(Auction.class).size()==0){

                  String auctionsString=   NetUtils.getResourceFromUrl(remote.getUrl());
                  List<Auction> auctions = AuctionUtils.buildAuctionsFromString(auctionsString,remote.getLastModified());
                  auctionsTemplate.insertAll(auctions);
//                  auctionsTemplate.removeAllFromAuctionUrls();
//                  auctionsTemplate.insertAuctionsUrlData(remote);
                   auctionsTemplate.updateAuctionsUrl(remote);

               }




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
