package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.repositories.ItemRepository;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import com.heim.wowauctions.service.SyncServiceContext;
import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;


/**
 * Created by
 * User: Sergey Benner
 * Date: 22.02.2009
 * Time: 0:40:17
 * Purpose : sync non existent items with the local db
 */


@Component
public class ItemsSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ItemsSyncService.class.getSimpleName());
    private final Semaphore semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());
    @Autowired
    private SyncServiceContext context;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MongoAuctionsDao auctionsDao;
    @Autowired
    private HttpReqHandler httpReqHandler;

    @Autowired
    private ItemRepository itemRepository;

    @Scheduled(fixedRate = 86400000)
    public void processItemsQueue() {
        logger.info("started");
        try {

            if (context.getQueue().isEmpty()) {
                //we just update the incoming items
                List<Long> allAuctionItemIds = getAuctionsDao().findAllAuctionItemIds(0);
                context.setQueue(AuctionUtils.createQueue(allAuctionItemIds));
                allAuctionItemIds.clear();
            } else {
                logger.info("Queue is not empty we dont add any items!");
            }

            while (!context.getQueue().isEmpty()) {
                logger.info("q size: " + context.getQueue().size());
                semaphore.acquire();
                taskExecutor.execute(
                        new ItemProcessorWorker(this, context.getQueue().poll()));
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }


    public ItemRepository getItemRepository() {
        return itemRepository;
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
