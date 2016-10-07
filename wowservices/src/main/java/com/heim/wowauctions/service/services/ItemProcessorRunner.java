package com.heim.wowauctions.service.services;

import com.heim.wowauctions.service.persistence.models.Item;
import com.heim.wowauctions.service.utils.AuctionUtils;
import com.heim.wowauctions.service.utils.NetUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/11/16
 * Time: 6:30 PM
 */


public class ItemProcessorRunner implements Runnable {

    private static final Logger logger = Logger.getLogger(ItemProcessorRunner.class);

    private static final String itemUrl = "http://us.battle.net/api/wow/item/";
    private long itemId;

    private ItemsSyncService service;


    @Autowired
    AsyncRestTemplate restTemplate;


    public ItemProcessorRunner(ItemsSyncService service,long itemId) {
        setService(service);
        setItemId(itemId);
    }

    public void run() {
        processItem();
        getService().releaseSemaphore();
    }

    private void processItem() {
        logger.info("processing " + getItemId());
        String url = itemUrl + getItemId();

        String itemReply = NetUtils.getResourceFromUrl(url);
        logger.info("got " + itemReply);
        String context = null;
        if (itemReply.contains("availableContexts")) {
            JSONArray jsonArray = new JSONObject(itemReply).getJSONArray("availableContexts");
            context = jsonArray.getString(0);
            if (context != null && !context.isEmpty()) {
                itemReply = NetUtils.getResourceFromUrl(url + "/" + context);
            }
        }

        Item item = null;
        item = AuctionUtils.buildItemFromString(itemReply);
        if (item != null) {
            getService().getAuctionsDao().save(item);
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
}