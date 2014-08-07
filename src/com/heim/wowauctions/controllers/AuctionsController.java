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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


@Controller
public class AuctionsController {


    Logger logger = Logger.getLogger(this.getClass().getName());


    private MongoAuctionsDao auctionsTemplate;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "/reborn", produces = "application/json")
    public
    @ResponseBody
    void getTest(HttpServletResponse res) throws IOException {

        AuctionUrl local = auctionsTemplate.getAuctionsUrl();

        List<Item> items = auctionsTemplate.find(new Query(), Item.class);
        List<Long> itemIds = new ArrayList<Long>();
        for (Item item : items)
            itemIds.add(item.getId());

        List<Auction> auctions = auctionsTemplate.getRebornsAuctions(itemIds, local.getLastModified());

        ObjectWriter objectWriter = objectMapper.writerWithView(Auction.class);

        auctions = AuctionUtils.buildAuctions(auctions, items);
        OutputStream outputStream;

        outputStream = res.getOutputStream();
        if (outputStream != null)
            objectWriter.writeValue(outputStream, auctions);
    }


    public MongoAuctionsDao getAuctionsTemplate() {
        return auctionsTemplate;
    }

    public void setAuctionsTemplate(MongoAuctionsDao auctionsTemplate) {
        this.auctionsTemplate = auctionsTemplate;
    }
}
