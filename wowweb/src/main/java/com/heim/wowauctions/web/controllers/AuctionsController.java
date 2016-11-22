package com.heim.wowauctions.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.service.persistence.models.ArchivedAuction;
import com.heim.wowauctions.service.persistence.models.Auction;
import com.heim.wowauctions.service.persistence.models.Item;
import com.heim.wowauctions.service.utils.AuctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;


@Controller
public class AuctionsController {

    @Autowired
    private MongoAuctionsDao auctionsDao;


    private final ObjectMapper objectMapper = new ObjectMapper();


    @RequestMapping(method = RequestMethod.GET, value = "/items", produces = "application/json"
    )
    public
    @ResponseBody
    void getItem(HttpServletResponse res, @RequestParam(value = "name", required = false) String name,
                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                 @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize,
                 @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        OutputStream outputStream;
        ObjectWriter objectWriter = objectMapper.writerWithView(Auction.class);
        outputStream = res.getOutputStream();
        if (name != null) {
            name = name.trim();
            name = name.replaceAll("[^a-zA-Z0-9 ']", "");
        }

        if (name == null || name.trim().isEmpty() ||
                name.trim().length() <= 1) {
            objectWriter.writeValue(outputStream, "");
        } else {

            //  AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            List<Item> items;
            if (!exact)
                items = auctionsDao.findItemByName(name);
            else
                items = auctionsDao.findItemByExactName(name);

            List<Long> itemIds = new ArrayList<Long>();
            for (Item item : items)
                itemIds.add(item.getId());


            Sort sort = new Sort(Sort.Direction.ASC, "buyout");

            PageRequest pageRequest;
            if (page == 0)
                pageRequest = new PageRequest(0, pageSize, sort);
            else
                pageRequest = new PageRequest(page, pageSize, sort);

            Page<Auction> auctions = auctionsDao.getAuctionsByItemIDs(itemIds, pageRequest);

            auctions = AuctionUtils.buildPagedAuctions(auctions, pageRequest, items);



            if (outputStream != null)
                objectWriter.writeValue(outputStream, auctions);

        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/itemchart", produces = "application/json"
    )
    public
    @ResponseBody
    void getItemChart(HttpServletResponse res, @RequestParam(value = "id", required = true) String id,
                      @RequestParam(value = "period", required = false) Integer period,
                      @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        ObjectWriter objectWriter = objectMapper.writerWithView(ArchivedAuction.class);
        OutputStream outputStream = res.getOutputStream();
        res.addHeader("Content-Type", "application/json;charset=utf-8");
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            objectWriter.writeValue(outputStream, "");
        }

        List<ArchivedAuction> auctions = null;
        if (id != null) {
            auctions = auctionsDao.getItemStatistics(Long.parseLong(id));
        }

        Map<Long, Long> map = new HashMap<Long, Long>();
        for (ArchivedAuction auction : auctions) {
            if (auction.getBuyout() != 0)
                map.put(auction.getBuyout() / auction.getQuantity(), auction.getTimestamp());
        }

        objectWriter.writeValue(outputStream, map.entrySet().toArray());
    }




}
