package com.heim.wowauctions.common.persistence.repositories;



import com.heim.wowauctions.common.persistence.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
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
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    Page<Item> findByName(String name, Pageable pageable);

    Item findByItemId(long itemId);

    Page<Item> findByNameLike(String name, Pageable pageable);

    @Query(value = "{'name': {$regex : ?0, $options: 'i'}}")
    List<Item> findItemsByNameRegex(String name);

    @Query(value = "{'name': {$regex : '^?0$', $options: 'i'}}")
    List<Item> findItemsByNameRegexExactMatch(String name);


}


