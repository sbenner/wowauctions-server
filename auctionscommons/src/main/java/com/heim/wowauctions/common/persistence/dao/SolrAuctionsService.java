package com.heim.wowauctions.common.persistence.dao;

import com.heim.wowauctions.common.persistence.models.*;
import com.heim.wowauctions.common.persistence.repositories.*;
import com.heim.wowauctions.common.utils.AuctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SolrAuctionsService {


    @Autowired
    private
    RealmRepository realmRepository;

    @Autowired
    private
    FeedbackRepository feedbackRepository;

    @Autowired
    private
    ItemRepository itemRepository;

    @Autowired
    private
    AuctionRepository auctionRepository;


    @Autowired
    private
    AuctionUrlRepository auctionUrlRepository;


    @Autowired
    private
    ArchivedAuctionRepository archivedAuctionRepository;

    @Autowired
    private ItemChartDataRepository itemChartDataRepository;


    public List<Auction> getAuctionsByItemIDs(List<Long> ids, Pageable pageable) {
        return auctionRepository.findByItemIdIn(ids);
    }

    //todo: build auctions with timestamp and ownerRealm if we go beyond 3 realms


    public AuctionUrl getAuctionUrl() {
        return auctionUrlRepository.findAll().stream().findFirst().orElse(null);
    }

    public void saveUrl(AuctionUrl url) {
        auctionUrlRepository.save(url);
    }

    public void updateUrl(AuctionUrl url) {

        auctionUrlRepository.save(url);
    }

    public Iterable<Item> findAllItems() {
        return itemRepository.findAll();
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

    public void removeArchivedAuctions(List<Auction> auctions) {
        auctionRepository.deleteAll(auctions);
    }

    public void saveFeedback(Feedback feedback){
        feedback.setTimestamp(System.currentTimeMillis());
        feedbackRepository.save(feedback);
    }

    public List<ArchivedAuction> getItemStatisticsByTimestamp(long itemId) {

        return archivedAuctionRepository.findByTimestampBetween(AuctionUtils.getTimestamp(true), AuctionUtils.getTimestamp(false), itemId);

    }

    public List<Auction> findAuctionsToArchive(long timestamp) {
        return auctionRepository.findAuctionByTimestampBefore(timestamp);
    }

    public void saveAuction(Auction auction) {
        auctionRepository.save(auction);
    }

    public void insertAllAuctions(List<Auction> auctions) {
        auctionRepository.saveAll(auctions);
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

    public Page<Item> findItemByName(String name, Pageable pageable) {

        return itemRepository.findByName(name, pageable);

    }

//    public List<Item >findItemByExactName(String name){
//
//    }

    public void deleteItemChartData() {
        itemChartDataRepository.deleteAll();
    }

    public void saveItemChart(ItemChartData data) {
        itemChartDataRepository.save(data);
    }

    public void saveItemCharts(Collection<ItemChartData> data) {
        itemChartDataRepository.saveAll(data);
    }


    public void saveRealms(List<Realm> realmList) {
        realmRepository.saveAll(realmList);
    }

}
