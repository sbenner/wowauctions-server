package com.heim.wowauctions.service.services;


import com.heim.wowauctions.service.SyncServiceContext;
import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync non existent items with the local db
 */


@Component
public class ItemsSyncService  {

    private static final Logger logger = Logger.getLogger(ItemsSyncService.class.getSimpleName());

    @Autowired
    SyncServiceContext context;
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired
    private MongoAuctionsDao auctionsDao;

    private final Semaphore semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());
    @Autowired
    private HttpReqHandler httpReqHandler;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        return taskExecutor;
    }

    @Scheduled(fixedRate = 120000)
    public void processItemsQueue() {
        logger.info("started");
        try {

            if (context.getQueue().isEmpty()) {
                List<Long> allAuctionItemIds = getAuctionsDao().findAllAuctionItemIds(0);
                Set<Long> existingItemIds = getAuctionsDao().getAllItemIDs();
                context.setQueue(AuctionUtils.createQueue(existingItemIds, allAuctionItemIds));
                allAuctionItemIds.clear();
                existingItemIds.clear();
            } else {
                logger.info("Queue is not empty we dont add any items!");
            }

            while (!context.getQueue().isEmpty()) {
                logger.info("q size: " + context.getQueue().size());
                semaphore.acquire();
                taskExecutor.execute(new ItemProcessorWorker(this, context.getQueue().poll()));
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void releaseSemaphore() {
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
