package com.heim.wowauctions.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import com.heim.wowauctions.utils.AuctionUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Controller
public class AuctionsController {


    Logger logger = Logger.getLogger(this.getClass().getName());


    private MongoAuctionsDao auctionsDao;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "/items{name}", produces = "application/json")    public
    @ResponseBody
    void getTest(HttpServletResponse res, @RequestParam(value="name", required=false) String name) throws IOException {

        AuctionUrl local = getAuctionsDao().getAuctionsUrl();

        List<Item> items = getAuctionsDao().findItemByName(name);

        List<Long> itemIds = new ArrayList<Long>();
        for (Item item : items)
            itemIds.add(item.getId());

        List<Auction> auctions = getAuctionsDao().getRebornsAuctions(itemIds, local.getLastModified());

        ObjectWriter objectWriter = objectMapper.writerWithView(Auction.class);

        auctions = AuctionUtils.buildAuctions(auctions, items);
        OutputStream outputStream;

        outputStream = res.getOutputStream();
        if (outputStream != null)
            objectWriter.writeValue(outputStream, auctions);
    }


    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
