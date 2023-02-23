package com.heim.wowauctions.service.services;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.dao.MongoService;
import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.Feedback;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.utils.HttpReqHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 10/24/17
 * Time: 3:42 AM
 */
@Component
public class AuctionsService {


    private static final ConcurrentHashMap<Long, String> cache = new ConcurrentHashMap<>();
    @Autowired
    HttpReqHandler httpReqHandler;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private MongoAuctionsDao mongoTemplate;

    @Value("${wow.tooltip.url}")
    private String tooltipUrl;

    public Page<Auction> getAuctions(String name, int pageSize, boolean exact, int page) {
        List<Item> items;
        if (!exact)
            items = mongoService.findItemByName(name);
        else
            items = mongoService.findItemByExactName(name);
        Map<Long, Item> m =
                items.stream().collect(Collectors.toMap(
                        Item::getItemId,
                        Function.identity(), (a, b) -> a
                ));


        Sort sort = Sort.by(Sort.Direction.ASC, "buyout");

        PageRequest pageRequest;
        if (page == 0)
            pageRequest = PageRequest.of(0, pageSize, sort);
        else
            pageRequest = PageRequest.of(page, pageSize, sort);

        return mongoService.getAuctionsByItemIDs(m, pageRequest);
    }

    public void saveFeedback(Feedback feedback){
        mongoService.saveFeedback(feedback);
    }

    public Map<String,Long> getCurrentStatus() {
        Map<String, Long> ret = new HashMap<>();
        ret.put("auctions_date", mongoTemplate.getAuctionsUrl().getLastModified());
        ret.put("count", mongoService.getAuctionsCount());
        ret.put("total", mongoService.getTotal());
        return ret;
    }


    public Map<Long, Long> getItemChart(long id) {
        return mongoService.getItemStatistics(id);
    }

    public String getTooltip(long id) {
        String localUrl = String.format(tooltipUrl, id);
        String out = cache.get(id);
        if (StringUtils.isEmpty(out)) {
            ResponseEntity<?> responseEntity = httpReqHandler.getData(localUrl);
            if(responseEntity != null && responseEntity.hasBody()) {
                HttpEntity body = (HttpEntity) responseEntity.getBody();
                if (body.hasBody()) {
                    out = body.toString().replaceAll("[\n\t\r]", "");
                    cache.put(id, out.trim());
                }
            }
        }
        return out;
    }

}
