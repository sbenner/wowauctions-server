package com.heim.wowauctions.common.persistence.dao;

import com.heim.wowauctions.common.persistence.models.*;
import com.heim.wowauctions.common.persistence.repositories.*;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MongoService {


    private final
    RealmRepository realmRepository;

    private final
    FeedbackRepository feedbackRepository;

    private final
    ItemRepository itemRepository;

    private final
    AuctionRepository auctionRepository;

    private final
    ArchivedAuctionRepository archivedAuctionRepository;

    private final ItemChartDataRepository itemChartDataRepository;

    public MongoService(RealmRepository realmRepository, FeedbackRepository feedbackRepository,
                        ItemRepository itemRepository, AuctionRepository auctionRepository, ArchivedAuctionRepository archivedAuctionRepository,
                        ItemChartDataRepository itemChartDataRepository
    ) {
        this.realmRepository = realmRepository;
        this.feedbackRepository = feedbackRepository;
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
        this.archivedAuctionRepository = archivedAuctionRepository;
        this.itemChartDataRepository = itemChartDataRepository;

    }


    public Page<Auction> getAuctionsByItemIDs(List<Long> ids, Pageable pageable) {
        return auctionRepository.findByItemIdIn(ids, pageable);
    }

    //todo: build auctions with timestamp and ownerRealm if we go beyond 3 realms


    public List<Item> findItemByName(String name) {
        return itemRepository
                .findItemsByNameRegex(name);
    }


    public List<Item> findItemByExactName(String name) {
        return itemRepository
                .findItemsByNameRegexExactMatch(name);
    }

    public long getAuctionsCount() {
        return auctionRepository.count();
    }

    public long getTotal() {
        return archivedAuctionRepository.count();
    }


    public List<Realm> getAllRealms() {

        return (ArrayList<Realm>) realmRepository.findAll();

    }

    public void saveFeedback(Feedback feedback) {
        feedback.setTimestamp(System.currentTimeMillis());
        feedbackRepository.save(feedback);
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
        realmRepository.saveAll(realmList);
    }

}
