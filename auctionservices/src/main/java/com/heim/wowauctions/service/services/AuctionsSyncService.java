package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import static java.lang.String.format;


/**
 * Created by
 * User: Sergey Benner
 */

@Component
public class AuctionsSyncService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionsSyncService.class);
    private final Semaphore semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());
    private final MongoAuctionsDao auctionsDao;
    private final HttpReqHandler httpReqHandler;
    private final TaskExecutor taskExecutor;
    private final String realm;
    @Value("${wow.auctions.url}")
    private String url;



    @Autowired
    public AuctionsSyncService(MongoAuctionsDao auctionsDao,
                               HttpReqHandler httpReqHandler,
                               TaskExecutor taskExecutor, String realm) {
        this.auctionsDao = auctionsDao;
        this.httpReqHandler = httpReqHandler;
        this.taskExecutor = taskExecutor;
        this.realm = realm;
    }

    @Scheduled(fixedRate = 180000)
    public synchronized void retrieveAuctions() {
        logger.debug("started");
        try {


            String out =
                    httpReqHandler.getData(url );
            if(StringUtils.isEmpty(out))
                return;

            AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);

            logger.info("local " + local.toString());
            logger.info("remote " + remote.toString());

            if (local.getLastModified() == null ||
                    local.getLastModified() < remote.getLastModified() ||
                    getAuctionsDao().getAuctionsCount() == 0) {

                logger.info(format("local.getLastModified() %s", local.getLastModified()));

                logger.info(format("remote.getLastModified() %s", remote.getLastModified()));

                logger.info(format("local.getLastModified() < remote.getLastModified() %s", local.getLastModified() < remote.getLastModified()));
                logger.info(format("getAuctionsDao().getAuctionsCount() %s", getAuctionsDao().getAuctionsCount()));

                //get new auctions
                String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (auctionsString != null) {
                    List<Auction> auctions = AuctionUtils.
                            buildAuctionsFromString(auctionsString, remote.getLastModified());

                    getAuctionsDao().insertAll(auctions);
                    BlockingQueue<Auction> queueToArchive = new LinkedBlockingQueue<Auction>();
                    queueToArchive.addAll(getAuctionsDao().findAuctionsToArchive(remote.getLastModified()));

                    getAuctionsDao().removeArchivedAuctions(remote.getLastModified());

                    while (!queueToArchive.isEmpty()) {
                        logger.info("q size: " + queueToArchive.size());
                        getSemaphore().acquire();
                        taskExecutor.execute(new ArchiveSaver(this, queueToArchive.poll()));
                    }

                    if (local.getUrl() == null) {
                        getAuctionsDao().insertAuctionsUrlData(remote);
                    } else {
                        getAuctionsDao().updateAuctionsUrl(remote);
                    }

                }

            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    Semaphore getSemaphore() {
        return semaphore;
    }
}
