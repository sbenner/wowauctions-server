package com.heim.wowauctions.service.services;

import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.persistence.models.Item;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.HttpReqHandler;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */

@Component
public class ItemProcessorRunner implements Runnable {

    private static final Logger logger = Logger.getLogger(ItemProcessorRunner.class);
    private static final String itemUrl = "https://us.api.battle.net/wow/item/";
    private long itemId;
    private ItemsSyncService service;
    private HttpReqHandler httpReqHandler;
    private MongoAuctionsDao mongoAuctionsDao;


    public ItemProcessorRunner() {
    }

    public ItemProcessorRunner(ItemsSyncService service, long itemId) {
        setService(service);
        setMongoAuctionsDao(service.getAuctionsDao());
        setHttpReqHandler(service.getHttpReqHandler());
        setItemId(itemId);
    }

    public void run() {
        processItem();
        getService().releaseSemaphore();
    }

    private void processItem() {
        logger.info("processing " + getItemId());
        String url = itemUrl + getItemId();

        String itemReply = getHttpReqHandler().getData(url);

        logger.info("got " + itemReply);
        String context = null;
        if (itemReply.contains("availableContexts")) {
            JSONArray jsonArray = new JSONObject(itemReply).getJSONArray("availableContexts");
            context = jsonArray.getString(0);
            if (context != null && !context.isEmpty()) {
                itemReply = getHttpReqHandler().getData(url + "/" + context);
            }
        }

        Item item = null;
        item = AuctionUtils.buildItemFromString(itemReply);
        if (item != null) {
            getMongoAuctionsDao().save(item);
            logger.info("saved ");
        }
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public ItemsSyncService getService() {
        return service;
    }

    public void setService(ItemsSyncService service) {
        this.service = service;
    }

    public HttpReqHandler getHttpReqHandler() {
        return httpReqHandler;
    }

    public void setHttpReqHandler(HttpReqHandler httpReqHandler) {
        this.httpReqHandler = httpReqHandler;
    }

    public MongoAuctionsDao getMongoAuctionsDao() {
        return mongoAuctionsDao;
    }

    public void setMongoAuctionsDao(MongoAuctionsDao mongoAuctionsDao) {
        this.mongoAuctionsDao = mongoAuctionsDao;
    }
}