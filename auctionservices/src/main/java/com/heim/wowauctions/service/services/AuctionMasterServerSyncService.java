package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.MongoService;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private MongoService mongoService;


    public void retrieveServerAuction() {
        logger.debug("started");
        try {

//            String realms = httpReqHandler.getData(url);
//
//            List<Realm> realmList = AuctionUtils.parseRealms(realms);
//            mongoService.saveRealms(realmList);

//            PriorityQueue<Realm>
//                    queueToArchive = new PriorityQueue<>();
//            queueToArchive.addAll(realmList);
//
//            while (queueToArchive.isEmpty()) {
//                Realm realm = queueToArchive.poll();
//                //we remove connected realms to avoid pulling the auctions for the same
//                //realms twice
//                for (final String realmSlug : realm.getConnectedRealms()) {
//                    realmList.stream().filter(r -> r.getSlug().equals(realmSlug))
//                            .collect(Collectors.toList()).forEach(
//                            queueToArchive::remove
//                    );
//
//                }
//            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }

    }

}
