package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.ItemChartData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface ItemChartDataRepository extends PagingAndSortingRepository<ItemChartData, Long> {

   // ItemChartData findByItemId(Long itemId, Pageable pageable);

    ItemChartData findByItemId(Long itemId);

   // ItemChartData findByTimestampBetween(long from, long to, long itemId);

}
