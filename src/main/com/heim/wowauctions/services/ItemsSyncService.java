package com.heim.wowauctions.services;


import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.utils.AuctionUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync non existent items with the local db
 */


public class ItemsSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(ItemsSyncService.class.getSimpleName());
    private MongoAuctionsDao auctionsDao;
    private ItemProcessor itemProcessor;

    public void run() {
        logger.info("started");
        try {

            List<Long> allAuctionItemIds = getAuctionsDao().findAllAuctionItemIds(0);
            List<Long> existingItemIds = getAuctionsDao().getAllItemIDs();
            List<Long> queue = AuctionUtils.createQueue(existingItemIds, allAuctionItemIds);
            getItemProcessor().processQueue(new ConcurrentLinkedQueue(queue));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ItemProcessor getItemProcessor() {
        return itemProcessor;
    }

    public void setItemProcessor(ItemProcessor itemProcessor) {
        this.itemProcessor = itemProcessor;
    }

    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
