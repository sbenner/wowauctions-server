package com.heim.wowauctions.service.services;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.persistence.repositories.ItemRepository;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */


public class ItemProcessorWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ItemProcessorWorker.class);
    private long itemId;
    private ItemsSyncService service;
    private HttpReqHandler httpReqHandler;
    private MongoAuctionsDao mongoAuctionsDao;
    private ItemRepository itemRepository;

    ItemProcessorWorker(ItemsSyncService service, long itemId) {
        setService(service);
        setMongoAuctionsDao(service.getAuctionsDao());
        setHttpReqHandler(service.getHttpReqHandler());
        setItemId(itemId);
        setItemRepository(service.getItemRepository());
    }

    public void run() {
        try {
            processItem();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            getService().releaseSemaphore();
        }

    }

    private void processItem() {

        long threadId = Thread.currentThread().getId();
        logger.info("Thread #" + threadId + " is processing item #" + getItemId());
        String url = getHttpReqHandler().getItemsUrl(getItemId());

        String itemReply = getHttpReqHandler().getData(url);

        logger.info("got " + itemReply);
        String context = null;
        if (!StringUtils.isEmpty(itemReply) && itemReply.contains("availableContexts")) {
            JSONArray jsonArray = new JSONObject(itemReply).getJSONArray("availableContexts");
            context = jsonArray.getString(0);
            if (context != null && !context.isEmpty()) {
                itemReply = getHttpReqHandler().getData(url + "/" + context);
            }
            Item item = AuctionUtils.buildItemFromString(itemReply);
            if (item != null) {
                Item foundItem = itemRepository.findByItemId(item.getItemId());
                if(foundItem!=null) {
                    foundItem.setName(item.getName());
                    foundItem.setItemLevel(item.getItemLevel());
                    foundItem.setQuality(item.getQuality());
                    itemRepository.save(foundItem);
                }else {
                    itemRepository.save(item);
                }
                logger.info("Thread #" + threadId + "  saved item #" + item.getItemId());
            }
        } else {
            logger.info("Thread #" + threadId + " is finished - NO REPLY");
        }


    }

    private long getItemId() {
        return itemId;
    }

    private void setItemId(long itemId) {
        this.itemId = itemId;
    }

    ItemsSyncService getService() {
        return service;
    }

    private void setService(ItemsSyncService service) {
        this.service = service;
    }

    private HttpReqHandler getHttpReqHandler() {
        return httpReqHandler;
    }

    private void setHttpReqHandler(HttpReqHandler httpReqHandler) {
        this.httpReqHandler = httpReqHandler;
    }

    private MongoAuctionsDao getMongoAuctionsDao() {
        return mongoAuctionsDao;
    }

    private void setMongoAuctionsDao(MongoAuctionsDao mongoAuctionsDao) {
        this.mongoAuctionsDao = mongoAuctionsDao;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
}