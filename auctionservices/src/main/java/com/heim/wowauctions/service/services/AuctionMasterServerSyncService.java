package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.SolrAuctionsService;
import com.heim.wowauctions.common.persistence.models.Realm;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Created by
 * User: Sergey Benner
 */

//we keep disabled it for now
@Component
public class AuctionMasterServerSyncService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuctionMasterServerSyncService.class);

    @Value("${wow.status.url}")
    String url;

    @Autowired
    HttpReqHandler httpReqHandler;
    @Autowired
    private SolrAuctionsService solrAuctionsService;

    @Scheduled(fixedRate = 3600000)
    public void retrieveServerAuction() {
        logger.debug("started");
        try {

            String realms = httpReqHandler.getData(url);

            List<Realm> realmList = AuctionUtils.parseRealms(realms);
            solrAuctionsService.saveRealms(realmList);

            PriorityQueue<Realm>
                    queueToArchive = new PriorityQueue<>();
            queueToArchive.addAll(realmList);

            while (queueToArchive.isEmpty()) {
                Realm realm = queueToArchive.poll();
                //we remove connected realms to avoid pulling the auctions for the same
                //realms twice
                for (final String realmSlug : realm.getConnectedRealms()) {
                    realmList.stream().filter(r -> r.getSlug().equals(realmSlug))
                            .collect(Collectors.toList()).forEach(
                            queueToArchive::remove
                    );

                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }

    }

}
