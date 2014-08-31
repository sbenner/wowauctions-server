package com.heim.wowauctions.utils;

import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.AuctionUrl;
import com.heim.wowauctions.models.Item;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 6:32 PM
 */
public class AuctionUtils {


    public static AuctionUrl parseAuctionFile(String contents) {
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

    public static String buildPrice(long price){
        String newprice="";
        try{
            String oldprice = Long.toString(price);

            int len = oldprice.length();
            if(len>4)
            {   newprice = oldprice.substring(0, len-4) + "g ";
                newprice += oldprice.substring(len-4, len-2) + "s ";
                newprice += oldprice.substring(len-2,len) + "c";
            }
            if(len>2&&len<=4)
            {
                newprice += oldprice.substring(0, len-2) + "s ";
                newprice += oldprice.substring(len-2,len) + "c";

            }
            if(len<=2)
                newprice += oldprice.substring(0,len) + "c";

        }catch (Exception e){
            System.out.println(price);
            e.printStackTrace();
        }

        return newprice;
    }



    public static long getTimestamp(boolean firstLast) {

        GregorianCalendar cal =
                (GregorianCalendar) GregorianCalendar.getInstance();
        cal.set(cal.HOUR, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);

        int day = firstLast ? cal.getMinimum(GregorianCalendar.DAY_OF_MONTH) : cal.getMaximum(GregorianCalendar.DAY_OF_MONTH);
        cal.set(cal.DAY_OF_MONTH, day);
        return cal.getTimeInMillis();
    }


    public static List<Auction> buildAuctionsFromString(String contents, long timestamp) {
        List<Auction> auctions = new ArrayList<Auction>();

        JSONObject jsonObject = new JSONObject(contents);
        JSONObject alliance = jsonObject.getJSONObject("alliance");
        JSONArray auctionsArray = alliance.getJSONArray("auctions");

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

    public static Item buildItemFromString(String in) {

        JSONObject obj = new JSONObject(in);
        Item item = new Item();
        item.setId(obj.getLong("id"));
        item.setName(obj.getString("name"));
        item.setItemLevel(obj.getInt("itemLevel"));
        item.setQuality(obj.getInt("quality"));

        return item;
    }


    public static List<Long> createQueue(List<Long> existingItems, List<Long> auctionItems) {
        List<Long> newQueue = new ArrayList<Long>();

        for (long auctionItem : auctionItems)
            if (existingItems.indexOf(auctionItem) == -1) {
                newQueue.add(auctionItem);
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
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return reborns;
    }

    @Deprecated
    public static List<Auction> buildAuctions(long timestamp) {
        List<Auction> auctions = new ArrayList<Auction>();
        File auctionsFile = new File("auctions.json");
        if (auctionsFile.exists()) {
            try {
                String contents = FileUtils.readFileToString(auctionsFile);
                JSONObject jsonObject = new JSONObject(contents);
                JSONObject alliance = jsonObject.getJSONObject("alliance");
                JSONArray auctionsArray = alliance.getJSONArray("auctions");

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


            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return auctions;
    }


}
