package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {

    Page<Auction> findByItemId(Long itemId, Pageable pageable);

    Page<Auction> findByItemIdIn(Set<Long> itemIDsList, Pageable pageable);


}
