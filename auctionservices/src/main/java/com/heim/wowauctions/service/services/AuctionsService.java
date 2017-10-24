package com.heim.wowauctions.service.services;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 10/24/17
 * Time: 3:42 AM
 */
@Component
public class AuctionsService {

    @Autowired
    private MongoAuctionsDao mongoService;

    public Page<Auction> getAuctions(String name,int pageSize,boolean exact,int page){
        //  AuctionUrl local = getAuctionsDao().getAuctionsUrl();
        List<Item> items;
        if (!exact)
            items = mongoService.findItemByName(name);
        else
            items = mongoService.findItemByExactName(name);

        List<Long> itemIds = new ArrayList<Long>();
        for (Item item : items)
            itemIds.add(item.getId());


        Sort sort = new Sort(Sort.Direction.ASC, "buyout");

        PageRequest pageRequest;
        if (page == 0)
            pageRequest = new PageRequest(0, pageSize, sort);
        else
            pageRequest = new PageRequest(page, pageSize, sort);

        Page<Auction> auctions = mongoService.getAuctionsByItemIDs(itemIds, pageRequest);

        return AuctionUtils.buildPagedAuctions(auctions, pageRequest, items);
    }


    public  Map<Long, Long>  getItemChart(long id){
       return mongoService.getItemStatistics(id);
    }

}