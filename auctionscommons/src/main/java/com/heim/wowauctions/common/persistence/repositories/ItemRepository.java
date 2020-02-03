package com.heim.wowauctions.common.persistence.repositories;


import com.heim.wowauctions.common.persistence.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/15/14
 * Time: 3:00 AM
 */
@Repository
public interface ItemRepository extends SolrCrudRepository<Item, String> {

    Page<Item> findByName(String name, Pageable pageable);
    Item findByItemId(long itemId);

    Page<Item> findByNameLike(String name, Pageable pageable);

//    @Query(value = "{'name': {$regex : ?0, $options: 'i'}}")
//    List<Item> findItemsByNameRegex(String name);
//
//    @Query(value = "{'name': {$regex : '^?0$', $options: 'i'}}")
//    List<Item> findItemsByNameRegexExactMatch(String name);


}


