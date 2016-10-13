package com.heim.wowauctions.service.services;


import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync non existent items with the local db
 */


@Component
public class ItemsSyncService extends TimerTask {

    private static final Logger logger = Logger.getLogger(ItemsSyncService.class.getSimpleName());

    @Autowired
    private MongoAuctionsDao auctionsDao;

    private Semaphore semaphore = new Semaphore(5);

    @Autowired
    private HttpReqHandler httpReqHandler;


    @Autowired
    private TaskExecutor taskExecutor;

    public void run() {
        logger.info("started");
        try {

            List<Long> allAuctionItemIds = getAuctionsDao().findAllAuctionItemIds(0);
            List<Long> existingItemIds = getAuctionsDao().getAllItemIDs();
            List<Long> queue = AuctionUtils.createQueue(existingItemIds, allAuctionItemIds);
            Queue<Long> q  =new ConcurrentLinkedQueue(queue);
            while (!q.isEmpty()) {
                semaphore.acquire();
                taskExecutor.execute(new ItemProcessorRunner(this,q.poll()));
            }

        } catch (Exception e) {
           logger.error(e.getMessage(),e);
        }

    }

    public void releaseSemaphore(){
        this.semaphore.release();
    }

    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }

    public HttpReqHandler getHttpReqHandler() {
        return httpReqHandler;
    }

    public void setHttpReqHandler(HttpReqHandler httpReqHandler) {
        this.httpReqHandler = httpReqHandler;
    }
}
