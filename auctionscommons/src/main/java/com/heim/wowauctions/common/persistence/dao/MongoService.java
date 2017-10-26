package com.heim.wowauctions.common.persistence.dao;

import com.heim.wowauctions.common.persistence.models.*;
import com.heim.wowauctions.common.persistence.repositories.*;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MongoService {


    @Autowired
    private
    RealmRepository realmRepository;

    @Autowired
    private
    ItemRepository itemRepository;

    @Autowired
    private
    AuctionRepository auctionRepository;

    @Autowired
    private
    ArchivedAuctionRepository archivedAuctionRepository;

    @Autowired
    private ItemChartDataRepository itemChartDataRepository;


    public Page<Auction> getAuctionsByItemIDs(List<Long> ids, Pageable pageable) {
        return auctionRepository.findByItemIdIn(ids, pageable);
    }

    //todo: build auctions with timestamp and ownerRealm if we go beyond 3 realms


    public List<Item> findItemByName(String name) {
        return itemRepository.findItemsByNameRegex(name);
    }


    public List<Item> findItemByExactName(String name) {
        return itemRepository.findItemsByNameRegexExactMatch(name);
    }

    public long getAuctionsCount() {
        return auctionRepository.count();
    }

    public List<Realm> getAllRealms() {

        return (ArrayList<Realm>) realmRepository.findAll();

    }

    public List<ArchivedAuction> getItemStatisticsByTimestamp(long itemId) {

        return archivedAuctionRepository.findByTimestampBetween(AuctionUtils.getTimestamp(true), AuctionUtils.getTimestamp(false), itemId);

    }


    public Map<Long, Long> getItemStatistics(long itemId) {
        ItemChartData
                item =
        itemChartDataRepository.findByItemId(itemId);
        if (item != null)
            return item.getValueTime();
        else
            return null;


    }

    public void deleteItemChartData() {
        itemChartDataRepository.deleteAll();
    }

    public ItemChartData saveItemChart(ItemChartData data) {
        return itemChartDataRepository.save(data);
    }


    public void saveRealms(List<Realm> realmList) {
        realmRepository.save(realmList);
    }

}
