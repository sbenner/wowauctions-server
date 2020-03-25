package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.dao.MongoService;
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
    final
    MongoService mongoService;
    private final HttpReqHandler httpReqHandler;
    private final TaskExecutor syncTaskExecutor;
    private final MongoAuctionsDao mongoTemplate;
    @Value("${wow.auctions.url}")
    private String url;

    @Value("${application.realm}")
    private String realm;

    @Autowired
    public AuctionsSyncService(MongoAuctionsDao mongoTemplate,
                               HttpReqHandler httpReqHandler,
                               TaskExecutor syncTaskExecutor,
                               MongoService mongoService) {
        this.mongoTemplate = mongoTemplate;
        this.httpReqHandler = httpReqHandler;
        this.syncTaskExecutor = syncTaskExecutor;
        this.mongoService = mongoService;
    }

    @Scheduled(fixedRate = 3600000,initialDelay = 180000)
    public synchronized void retrieveAuctions() {
        logger.debug("started");
        try {


            String out =
                    httpReqHandler.getData(url);
            if (StringUtils.isEmpty(out))
                return;

            AuctionUrl local = mongoTemplate.getAuctionsUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);

            logger.info("local " + local.toString());
            logger.info("remote " + remote.toString());

            if (local.getLastModified() == null ||
                    local.getLastModified() < remote.getLastModified() ||
                    mongoService.getAuctionsCount() == 0) {

                logger.info(format("local.getLastModified() %s", local.getLastModified()));

                logger.info(format("remote.getLastModified() %s", remote.getLastModified()));

                logger.info(format("local.getLastModified() < remote.getLastModified() %s", local.getLastModified() < remote.getLastModified()));
                logger.info(format("auctionsDao.getAuctionsCount() %s", mongoService.getAuctionsCount()));

                //get new auctions
                String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (auctionsString != null) {
                    List<Auction> auctions = AuctionUtils.
                            buildAuctionsFromString(auctionsString, remote.getLastModified());

                    mongoTemplate.insertAll(auctions);
                    BlockingQueue<Auction> queueToArchive = new LinkedBlockingQueue<Auction>();
                    queueToArchive.addAll(mongoTemplate.findAuctionsToArchive(remote.getLastModified()));

                    mongoTemplate.removeArchivedAuctions(remote.getLastModified());

                    while (!queueToArchive.isEmpty()) {
                        logger.info("q size: " + queueToArchive.size());
                        getSemaphore().acquire();
                        syncTaskExecutor.execute(
                                new ArchiveSaver(this, queueToArchive.poll(), mongoTemplate));
                    }

                    if (local.getUrl() == null) {
                        mongoTemplate.insertAuctionsUrlData(remote);
                    } else {
                        mongoTemplate.updateAuctionsUrl(remote);
                    }

                }

            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }


    Semaphore getSemaphore() {
        return semaphore;
    }
}
