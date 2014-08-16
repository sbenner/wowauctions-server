package com.heim.wowauctions.repositories;

import com.heim.wowauctions.models.ArchivedAuction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface ArchivedAuctionRepository extends PagingAndSortingRepository<ArchivedAuction, Long> {

    Page<ArchivedAuction> findByItemId(Long itemId, Pageable pageable);

    List<ArchivedAuction> findByItemId(Long itemId);

    List<ArchivedAuction> findByTimestampBetween(long from,long to,long itemId);

}
