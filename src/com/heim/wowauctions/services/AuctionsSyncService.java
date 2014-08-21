package com.heim.wowauctions.services;


import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.utils.AuctionUtils;
import com.heim.wowauctions.utils.NetUtils;
import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;


import java.util.List;
import java.util.Queue;
import java.util.TimerTask;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync xmls with db
 */


public class AuctionsSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(AuctionsSyncService.class);
    private static final String url = "http://us.battle.net/api/wow/auction/data/veknilash";
    private MongoAuctionsDao auctionsDao;


    public void run() {
        logger.debug("started");
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

                if(auctionsString!=null){
                List<Auction> auctions = AuctionUtils.buildAuctionsFromString(auctionsString, remote.getLastModified());
                getAuctionsDao().insertAll(auctions);

                //archive old
                List<Auction> toArchive = getAuctionsDao().findAuctionsToArchive(remote.getLastModified());
                getAuctionsDao().archiveAuctions(toArchive);
                getAuctionsDao().removeArchivedAuctions(remote.getLastModified());


                if(local==null){
                  getAuctionsDao().insertAuctionsUrlData(remote);
                }else{
                     getAuctionsDao().updateAuctionsUrl(remote);
                }

                }

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
