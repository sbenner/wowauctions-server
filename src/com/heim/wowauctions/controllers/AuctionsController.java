package com.heim.wowauctions.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.ArchivedAuction;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import com.heim.wowauctions.utils.AuctionUtils;
import org.apache.log4j.Logger;
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

    private MongoAuctionsDao auctionsDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "/items", produces = "application/json"
    )
    public
    @ResponseBody
    void getItem(HttpServletResponse res, @RequestParam(value = "name", required = false) String name,
                 @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        OutputStream outputStream;
        ObjectWriter objectWriter = objectMapper.writerWithView(Auction.class);
        outputStream = res.getOutputStream();
        res.addHeader("Content-Type","application/json;charset=utf-8");
        if (name != null) {
            name=name.trim();
            name=name.replaceAll("[^\\w\\s]","");
            AuctionUrl local = getAuctionsDao().getAuctionsUrl();
            List<Item> items;
            if (!exact)
                items = getAuctionsDao().findItemByName(name);
            else
                items = getAuctionsDao().findItemByExactName(name);

            List<Long> itemIds = new ArrayList<Long>();
            for (Item item : items)
                itemIds.add(item.getId());

            List<Auction> auctions = getAuctionsDao().getRebornsAuctions(itemIds, local.getLastModified());
            auctions = AuctionUtils.buildAuctions(auctions, items);

            if (outputStream != null)
                objectWriter.writeValue(outputStream, auctions);

        } else {
            objectWriter.writeValue(outputStream, "");
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
        OutputStream    outputStream = res.getOutputStream();
        res.addHeader("Content-Type","application/json;charset=utf-8");
        try{Long.parseLong(id);}catch (NumberFormatException e){
            objectWriter.writeValue(outputStream,"");
        }


        List<ArchivedAuction> auctions=null;
        if (id != null) {
             auctions = getAuctionsDao().getItemStatistics(Long.parseLong(id));
        }
        objectWriter.writeValue(outputStream,auctions);
    }




    public MongoAuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(MongoAuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }
}
