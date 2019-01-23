package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.SolrAuctionsService;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.persistence.repositories.ArchivedAuctionRepository;
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

    private final HttpReqHandler httpReqHandler;
    private final TaskExecutor taskExecutor;
    final
    SolrAuctionsService solrAuctionsService;
    @Autowired
    ArchivedAuctionRepository archivedAuctionRepository;
    //  private final String realm;
    @Value("${wow.auctions.url}")
    private String url;

    @Autowired
    public AuctionsSyncService(HttpReqHandler httpReqHandler,
                               TaskExecutor taskExecutor,
                               //    String realm,
                               SolrAuctionsService solrAuctionsService) {

        this.httpReqHandler = httpReqHandler;
        this.taskExecutor = taskExecutor;
        //this.realm = realm;
        this.solrAuctionsService = solrAuctionsService;
    }

    @Scheduled(fixedRate = 180000,initialDelay = 180000)
    public synchronized void retrieveAuctions() {
        logger.debug("started");
        try {


            String out =
                    httpReqHandler.getData(url );
            if(StringUtils.isEmpty(out))
                return;

            AuctionUrl local = solrAuctionsService.getAuctionUrl();
            AuctionUrl remote = AuctionUtils.parseAuctionFile(out);

            if (local != null && remote != null) {
                logger.info("local " + local.toString());
                logger.info("remote " + remote.toString());

                logger.info(format("local.getLastModified() %s", local.getLastModified()));

                logger.info(format("remote.getLastModified() %s", remote.getLastModified()));

                logger.info(format("local.getLastModified() < remote.getLastModified() %s", local.getLastModified() < remote.getLastModified()));
                logger.info(format("auctionsDao.getAuctionsCount() %s", solrAuctionsService.getAuctionsCount()));

            }
            if (local == null || local.getLastModified() == null ||
                    (remote!=null &&
                    local.getLastModified() < remote.getLastModified()) ||
                    solrAuctionsService.getAuctionsCount() == 0) {

                //get new auctions

                String auctionsString = httpReqHandler.getData(remote.getUrl());

                if (auctionsString != null) {
                    List<Auction> auctions = AuctionUtils.
                            buildAuctionsFromString(auctionsString, remote.getLastModified());

                    solrAuctionsService.insertAllAuctions(auctions);

                    List<Auction> toArchive = solrAuctionsService
                            .findAuctionsToArchive(remote.getLastModified());

                    // auctionsDao.removeArchivedAuctions(remote.getLastModified());

//                    while (!queueToArchive.isEmpty()) {
  //                      logger.info("q size: " + queueToArchive.size());
    //                    getSemaphore().acquire();
                        taskExecutor.execute(
                        //        new ArchiveSaver(this, queueToArchive.poll(), solrAuctionsService)
                                ()-> solrAuctionsService.saveToArchive(toArchive)

                        );
                    //}
                    if(toArchive.size()>0)
                        solrAuctionsService.removeArchivedAuctions(toArchive);

                    if (local == null || local.getUrl() == null) {
                        solrAuctionsService.saveUrl(remote);
                    } else {
                        solrAuctionsService.updateUrl(remote);
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
