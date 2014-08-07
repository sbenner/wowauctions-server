package com.heim.wowauctions.controllers;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.heim.wowauctions.dao.AuctionsDao;
import com.heim.wowauctions.dao.MongoAuctionsDao;
import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import com.heim.wowauctions.utils.AuctionUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AuctionsController {



    Logger logger = Logger.getLogger(this.getClass().getName());

    private AuctionsDao auctionsDao;


    private MongoAuctionsDao auctionsTemplate;

	private Jaxb2Marshaller jaxb2Mashaller;
	
	public void setJaxb2Mashaller(Jaxb2Marshaller jaxb2Mashaller) {
		this.jaxb2Mashaller = jaxb2Mashaller;
	}

private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method=RequestMethod.GET, value="/reborn", produces = "application/json")
    public @ResponseBody void getTest(HttpServletResponse res) throws IOException {

          AuctionUrl local = auctionsTemplate.getAuctionsUrl();

         List<Item> items = auctionsTemplate.find(new Query(),Item.class);
         List<Long> itemIds = new ArrayList<Long>();
         for(Item item:items)
             itemIds.add(item.getId());

         List<Auction> auctions = auctionsTemplate.getRebornsAuctions(itemIds,local.getLastModified());

         ObjectWriter objectWriter = objectMapper.writerWithView(Auction.class);



        //   objectWriter.with(SerializationFeature.INDENT_OUTPUT);

         auctions = AuctionUtils.buildAuctions(auctions, items);
           OutputStream outputStream;

        outputStream = res.getOutputStream();
        if(outputStream!=null)
           objectWriter.writeValue(outputStream, auctions);
    }

    //http://172.16.4.19/cylab.ws.url.php?urlid=275924340&action=x
    @RequestMapping("/reborns.json")
      public void test2(HttpServletRequest req,HttpServletResponse response) throws IOException {

		OutputStream os = response.getOutputStream();
        Date date= new java.util.Date();
        Timestamp ts =    new Timestamp(date.getTime());
        Enumeration params = req.getParameterNames();
        response.setContentType("application/json; charset=UTF-8");

//        List<Auction> auctionList = buildAuctions();
//        logger.info(AuctionUtils.findAuctions(auctionList).toString());
        AuctionUrl local = auctionsTemplate.getAuctionsUrl();

        List<Item> items = auctionsTemplate.find(new Query(),Item.class);
        List<Long> itemIds = new ArrayList<Long>();
         for(Item item:items)
               itemIds.add(item.getId());

        List<Auction> auctions = auctionsTemplate.getRebornsAuctions(itemIds,local.getLastModified());

        auctions = AuctionUtils.buildAuctions(auctions, items);

         ///get latest timestamp from auctionurl
        //retrieve items from the auctions with that timestamp
        //$in ids



        os.write(auctions.toString().getBytes());

	}



    public AuctionsDao getAuctionsDao() {
        return auctionsDao;
    }

    public void setAuctionsDao(AuctionsDao auctionsDao) {
        this.auctionsDao = auctionsDao;
    }

    public MongoAuctionsDao getAuctionsTemplate() {
        return auctionsTemplate;
    }

    public void setAuctionsTemplate(MongoAuctionsDao auctionsTemplate) {
        this.auctionsTemplate = auctionsTemplate;
    }
}
