package com.heim.wowauctions.service.services;


import com.heim.wowauctions.common.persistence.dao.SolrAuctionsService;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.Feedback;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 10/24/17
 * Time: 3:42 AM
 */
@Component
public class AuctionsService {


    private static ConcurrentHashMap<Long, String> cache = new ConcurrentHashMap<>();
    @Autowired
    HttpReqHandler httpReqHandler;


    @Autowired
    private SolrAuctionsService auctionsService;
//
//    @Autowired
//    private MongoAuctionsDao auctionsDao;

    @Value("${wow.tooltip.url}")
    private String tooltipUrl;

    public List<Auction> getAuctions(String name, int pageSize, boolean exact, int page) {
        //  AuctionUrl local = getAuctionsDao().getAuctionsUrl();
        Page<Item> items;


//        if (page == 0)
//            pageRequest = new PageRequest(0, pageSize, sort);
//        else
//            pageRequest = new PageRequest(page, pageSize, sort);
        //if (!exact)
        items = auctionsService.findItemByName(name, PageRequest.of(page, pageSize));
//        else
//            items = auctionsService.findItemByExactName(name);

        List<Long> itemIds =
                (List<Long>) items.getContent().stream().mapToLong(i -> i.getItemId());


        Sort sort = new Sort(Sort.Direction.ASC, "buyout");

        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
//        if (page == 0)
//            pageRequest = new PageRequest(0, pageSize, sort);
//        else
//            pageRequest = new PageRequest(page, pageSize, sort);

        List<Auction> auctions = auctionsService.getAuctionsByItemIDs(itemIds, pageRequest);

        return auctions;
    }

    public void saveFeedback(Feedback feedback){
        auctionsService.saveFeedback(feedback);
    }

    public Map<String,Long> getCurrentStatus(){
        Map<String,Long> ret = new HashMap<>();
        ret.put("auctions_date", auctionsService.getAuctionUrl().getLastModified());
        ret.put("count", auctionsService.getAuctionsCount());
        ret.put("total", auctionsService.getTotal());
        return ret;
    }

    public List<Long> findAllItemIds() {
        Iterator i = auctionsService.findAllItems().iterator();
        List<Long> ids = new ArrayList<>();
        while (i.hasNext()) {
            Item item = (Item) i.next();
            ids.add(item.getItemId());
        }
        return ids;
    }

    public Map<Long, Long> getItemChart(long id) {
        return auctionsService.getItemStatistics(id);
    }

    public String getTooltip(long id) {
        String localurl = String.format(tooltipUrl, id);
        String out = cache.get(id);
        if (StringUtils.isEmpty(out)) {
            out = httpReqHandler.getData(localurl).replaceAll("[\n\t\r]", "");
            cache.put(id, out.trim());
        }
        return out;
    }

}
