package com.heim.wowauctions.common.utils;

import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.persistence.models.Item;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 6:32 PM
 */
public class AuctionUtils {

    private static final Logger logger = Logger.getLogger(AuctionUtils.class);

    public static AuctionUrl parseAuctionFile(String contents) {
        logger.info("remote url: "+contents);
        AuctionUrl auctionUrl = new AuctionUrl();
        JSONObject jsonObject = new JSONObject(contents);
        JSONArray files = jsonObject.getJSONArray("files");
        auctionUrl.setLastModified(((JSONObject) files.get(0)).getLong("lastModified"));
        auctionUrl.setUrl(((JSONObject) files.get(0)).getString("url"));


        return auctionUrl;
    }

    public static Page<Auction> buildPagedAuctions(Page<Auction> auctions, Pageable pageable, List<Item> rebornsList) {

        List<Auction> foundAuctions = new ArrayList<Auction>();

        for (Item reborn : rebornsList) {

            for (Auction auction : auctions.getContent()) {
                auction.setPpi();
                if (reborn.getId() == auction.getItemId()) {
                    auction.setItem(reborn);
                    auction.setOwner(auction.getOwner() + "-" + auction.getOwnerRealm());
                    foundAuctions.add(auction);
                }

            }
        }
        Collections.sort(foundAuctions);
        return new PageImpl<Auction>(foundAuctions, pageable, auctions.getTotalElements());
    }

    public static String buildPrice(long price) {
        String newprice = "";
        try {
            String oldprice = Long.toString(price);

            int len = oldprice.length();
            if (len > 4) {
                newprice = oldprice.substring(0, len - 4) + "g ";
                newprice += oldprice.substring(len - 4, len - 2) + "s ";
                newprice += oldprice.substring(len - 2, len) + "c";
            }
            if (len > 2 && len <= 4) {
                newprice += oldprice.substring(0, len - 2) + "s ";
                newprice += oldprice.substring(len - 2, len) + "c";

            }
            if (len <= 2)
                newprice += oldprice.substring(0, len) + "c";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return newprice;
    }

    public static long getTimestamp(boolean firstLast) {

        GregorianCalendar cal =
                (GregorianCalendar) GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        int day = firstLast ? cal.getMinimum(GregorianCalendar.DAY_OF_MONTH) : cal.getMaximum(GregorianCalendar.DAY_OF_MONTH);
        cal.set(cal.DAY_OF_MONTH, day);
        return cal.getTimeInMillis();
    }

    public static List<Auction> buildAuctionsFromString(String contents, long timestamp) {
        List<Auction> auctions = new ArrayList<Auction>();

        JSONObject jsonObject = new JSONObject(contents);
        //   JSONObject alliance = jsonObject.getJSONObject("auctions");
        JSONArray auctionsArray = jsonObject.getJSONArray("auctions");

        for (int i = 0; i < auctionsArray.length(); i++) {
            JSONObject obj = (JSONObject) auctionsArray.get(i);
            Auction auction = new Auction();
            auction.setAuc(obj.getLong("auc"));
            auction.setItemId(obj.getLong("item"));
            auction.setBid(obj.getLong("bid"));
            auction.setBuyout(obj.getLong("buyout"));
            auction.setOwner(obj.getString("owner"));
            auction.setOwnerRealm(obj.getString("ownerRealm"));
            auction.setQuantity(obj.getInt("quantity"));
            auction.setTimeLeft(obj.getString("timeLeft"));


            auction.setTimestamp(timestamp);
            auctions.add(auction);
        }


        return auctions;
    }

    public static Item buildItemFromString(String in) throws JSONException {

        JSONObject obj = new JSONObject(in);
        Item item = new Item();

        item.setId(obj.getLong("id"));
        item.setName(obj.getString("name"));
        item.setItemLevel(obj.getInt("itemLevel"));
        item.setQuality(obj.getInt("quality"));

        return item;
    }

    public static BlockingQueue<Long> createQueue(Set<Long> existingItems, List<Long> auctionItems) {
        BlockingQueue<Long> newQueue = new LinkedBlockingQueue<Long>();

        for (long auctionItem : auctionItems) {
            if (!existingItems.contains(auctionItem)) {
                newQueue.add(auctionItem);
            }
        }

        return newQueue;
    }

    @Deprecated
    public static List<Item> makeReborns() {
        List<Item> reborns = new ArrayList<Item>();
        File rebornsFile = new File("reborns.json");
        if (rebornsFile.exists()) {
            try {
                String contents = FileUtils.readFileToString(rebornsFile);
                JSONArray rebornsJSonArray = new JSONArray(contents);
                for (int i = 0; i < rebornsJSonArray.length(); i++) {
                    JSONObject obj = (JSONObject) rebornsJSonArray.get(i);
                    Item reborn = new Item();
                    reborn.setId(obj.getLong("id"));
                    reborn.setName(obj.getString("name"));
                    reborn.setItemLevel(obj.getInt("itemLevel"));
                    reborn.setQuality(obj.getInt("quality"));
                    reborns.add(reborn);
                }


            } catch (IOException e) {
                logger.error(e.getMessage(), e);

            }
        }

        return reborns;
    }


}