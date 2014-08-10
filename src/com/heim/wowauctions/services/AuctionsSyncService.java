package com.heim.wowauctions.services;


import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.utils.AuctionUtils;
import com.heim.wowauctions.utils.NetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimerTask;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */


public class AuctionsSyncService extends TimerTask {

    protected static long ONE_DAY = 24 * 3600 * 1000L;
    private static final Logger logger = LoggerFactory.getLogger(AuctionsSyncService.class);
    private static String url = "http://us.battle.net/api/wow/auction/data/veknilash";
    //   private TransitionManager manager;
//    Date date = new Date( System.currentTimeMillis() - ONE_DAY );
//    Properties props;
//    SimpleDateFormat ts= new SimpleDateFormat("yyyy-MM-dd");
    private MongoAuctionsDao auctionsDao;
    //constructor


    public void run() {
        logger.debug("task started");
        try {

            String out =
                    NetUtils.getResourceFromUrl(url);

            AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);


            if (local == null ||
                    local.getLastModified() < remote.getLastModified() ||
                    getAuctionsDao().findAll(Auction.class).size() == 0) {

                //get new auctions
                String auctionsString = NetUtils.getResourceFromUrl(remote.getUrl());
                List<Auction> auctions = AuctionUtils.buildAuctionsFromString(auctionsString, remote.getLastModified());
                getAuctionsDao().insertAll(auctions);

                //archive old
                List<Auction> toArchive = getAuctionsDao().findAuctionsToArchive(remote.getLastModified());
                getAuctionsDao().archiveAuctions(toArchive);
                getAuctionsDao().removeArchivedAuctions(remote.getLastModified());


                if(local==null){
                    getAuctionsDao().insertAuctionsUrlData(remote);
                }
                getAuctionsDao().updateAuctionsUrl(remote);

            }


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
