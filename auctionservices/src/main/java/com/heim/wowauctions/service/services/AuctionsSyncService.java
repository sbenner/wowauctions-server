package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.dao.MongoService;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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

    @Scheduled(fixedRate = 3600000, initialDelay = 10000)
    public synchronized void retrieveAuctions() {
        logger.debug("started");
        try {


            ResponseEntity res =
                    httpReqHandler.getData(url);

            if (!res.getStatusCode().is2xxSuccessful())
                return;

            DateTimeFormatter FMT = new DateTimeFormatterBuilder()
                    .appendPattern("EEE, d MMM yyyy HH:mm:ss zzz")
                    .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                    .toFormatter()
                    .withZone(ZoneId.of("Europe/Paris"));

            String lm = res.getHeaders().get("Last-Modified").toString();
            Instant lastModified = FMT.parse(lm.substring(1, lm.length() - 1), Instant::from);
            String out = res.getBody().toString();

            JSONObject object = new JSONObject(out);
            AuctionUrl remote = AuctionUtils.parseAuctionFile(object, lastModified);
            AuctionUrl local = mongoTemplate.getAuctionsUrl();

//            logger.info("local " + local.toString());
//            logger.info("remote " + remote.toString());
//
            if ((local == null || local.getLastModified() == null) ||
                    local.getLastModified() < remote.getLastModified() ||
                    mongoService.getAuctionsCount() == 0) {

                if (local != null && local.getLastModified() != null) {
                    logger.info(format("local.getLastModified() %s", local.getLastModified()));
                    logger.info(format("local.getLastModified() < remote.getLastModified() %s", local.getLastModified() < remote.getLastModified()));
                }
                logger.info(format("remote.getLastModified() %s", remote.getLastModified()));

                logger.info(format("auctionsDao.getAuctionsCount() %s", mongoService.getAuctionsCount()));

                //get new auctions
                //String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (out != null) {
                    List<Auction> auctions = AuctionUtils.
                            buildAuctionsFromString(object, remote.getLastModified());

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

                    if (local == null || local.getUrl() == null) {
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
