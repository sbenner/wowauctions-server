package com.heim.wowauctions.service.rest.controllers;

import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.utils.AuctionUtils;
import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class AuctionsController {

    @Autowired
    private MongoAuctionsDao mongoService;


    @RequestMapping(method = RequestMethod.GET, value = "/items", produces = "application/json"
    )
    public
    @ResponseBody
    ResponseEntity getItem(HttpServletResponse res,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                           @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize,
                           @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        if (name != null) {
            name = name.trim();
            name = name.replaceAll("[^a-zA-Z0-9 ']", "");
        }

        if (name == null || name.trim().isEmpty() ||
                name.trim().length() <= 1) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {

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

            auctions = AuctionUtils.buildPagedAuctions(auctions, pageRequest, items);
            return new ResponseEntity(auctions, HttpStatus.OK);

        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/itemchart", produces = "application/json"
    )
    public
    @ResponseBody
    ResponseEntity getItemChart(HttpServletResponse res, @RequestParam(value = "id", required = true) String id,
                                @RequestParam(value = "period", required = false) Integer period,
                                @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        //  ObjectWriter objectWriter = objectMapper.writerWithView(ArchivedAuction.class);
        // OutputStream outputStream = res.getOutputStream();
        res.addHeader("Content-Type", "application/json;charset=utf-8");
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (id != null) {
            Map<Long, Long> auctions = mongoService.getItemStatistics(Long.parseLong(id));
            if (auctions != null && auctions.size() > 0) {
                return new ResponseEntity(auctions, HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


    }


}
