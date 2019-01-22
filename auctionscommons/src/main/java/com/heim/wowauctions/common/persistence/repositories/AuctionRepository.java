package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Auction;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface AuctionRepository extends SolrCrudRepository<Auction, String> {

    List<Auction> findByItemId(Long itemId);

    List<Auction> findByItemIdIn(List<Long> itemIDsList);


    List<Auction> findAuctionByTimestampBefore(Long ts);

}
