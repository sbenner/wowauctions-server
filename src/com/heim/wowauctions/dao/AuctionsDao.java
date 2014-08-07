package com.heim.wowauctions.dao;

import com.heim.wowauctions.models.Auction;
import com.heim.wowauctions.models.Item;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 6:34 PM
 */
public class AuctionsDao {


    public AuctionsDao(){

    }


    public static List<Auction> findAuctions(List<Auction> auctions) {
        List<Auction> foundAuctions = new ArrayList<Auction>();
        List<Item> rebornsList = makeReborns();
        for (Item reborn : rebornsList) {
            for (Auction auction : auctions) {

                if (reborn.getId() == auction.getItem()  ) {
                    auction.setItemName(reborn.getName());
                    auction.setItemLevel(reborn.getItemLevel());
                    foundAuctions.add(auction);
                }
            }

        }

        return foundAuctions;
    }


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


    public static List<Auction> buildAuctionsFromString(String contents,long timestamp) {
        List<Auction> auctions = new ArrayList<Auction>();
//        File auctionsFile = new File("auctions.json");
//        if (auctionsFile.exists()) {
//            try {

                JSONObject jsonObject = new JSONObject(contents);
                JSONObject alliance = jsonObject.getJSONObject("alliance");
                JSONArray auctionsArray = alliance.getJSONArray("auctions");

                for (int i = 0; i < auctionsArray.length(); i++) {
                    JSONObject obj = (JSONObject) auctionsArray.get(i);
                    Auction auction = new Auction();
                    auction.setAuc(obj.getLong("auc"));
                    auction.setItem(obj.getLong("item"));
                    auction.setBid(obj.getLong("bid"));
                    auction.setBuyout(obj.getLong("buyout"));
                    auction.setOwner(obj.getString("owner"));
                    auction.setOwnerRealm(obj.getString("ownerRealm"));
                    auction.setQuantity(obj.getInt("quantity"));
                    auction.setTimeLeft(obj.getString("timeLeft"));
                    auction.setTimestamp(timestamp);
                    auctions.add(auction);
                }


//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }

        return auctions;
    }

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
                    auction.setItem(obj.getLong("item"));
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
