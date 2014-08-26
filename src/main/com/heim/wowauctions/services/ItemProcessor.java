package com.heim.wowauctions.services;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/9/14
 * Time: 10:32 PM
 */
import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Item;
import com.heim.wowauctions.utils.AuctionUtils;
import com.heim.wowauctions.utils.NetUtils;
import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;


import java.util.Queue;

public class ItemProcessor {

    Logger logger = Logger.getLogger(ItemProcessor.class);

    private TaskExecutor taskExecutor;
    private MongoAuctionsDao auctionsDao;


    public void processQueue(Queue q)
    {
        while(!q.isEmpty())
            taskExecutor.execute(new ItemProcessorTask((Long)q.poll()));
    }


    public ItemProcessor(TaskExecutor taskExecutor,MongoAuctionsDao auctionsDao) {
        this.taskExecutor = taskExecutor;
        setAuctionsDao(auctionsDao);

    }
    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }


    private class ItemProcessorTask implements Runnable {


        private long itemId;
        private static final String itemUrl="http://us.battle.net/api/wow/item/";

        public ItemProcessorTask(long itemId) {
            this.setItemId(itemId);

        }

        public void run() {
            logger.info("processing "+this.getItemId());
            String url = this.itemUrl+this.getItemId();
            String itemReply = NetUtils.getResourceFromUrl(url);
            logger.info("got "+itemReply);
            Item item = AuctionUtils.buildItemFromString(itemReply);

            getAuctionsDao().save(item);
            logger.info("saved ");

        }

        public long getItemId() {
            return itemId;
        }

        public void setItemId(long itemId) {
            this.itemId = itemId;
        }
    }



}


