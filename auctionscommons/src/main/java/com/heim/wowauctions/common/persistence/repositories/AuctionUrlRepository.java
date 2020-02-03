package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.AuctionUrl;
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
public interface AuctionUrlRepository extends PagingAndSortingRepository<AuctionUrl, String> {

    List<AuctionUrl> findAll();

}
