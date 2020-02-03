package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.ItemChartData;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface ItemChartDataRepository extends SolrCrudRepository<ItemChartData, String> {

   // ItemChartData findByItemId(Long itemId, Pageable pageable);

    ItemChartData findByItemId(Long itemId);

   // ItemChartData findByTimestampBetween(long from, long to, long itemId);

}
