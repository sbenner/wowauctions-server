package com.heim.wowauctions.common.utils;

import com.heim.wowauctions.common.persistence.models.Auction;
import com.heim.wowauctions.common.persistence.models.AuctionUrl;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.persistence.models.Realm;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/6/14
 * Time: 6:32 PM
 */
public class AuctionUtils {

    private static final Logger logger = Logger.getLogger(AuctionUtils.class);

    public static AuctionUrl parseAuctionFile(String contents) {
        logger.info("remote url: " + contents);
        AuctionUrl auctionUrl = new AuctionUrl();
        JSONObject jsonObject = new JSONObject(contents);
        JSONArray files = jsonObject.getJSONArray("files");
        auctionUrl.setLastModified(((JSONObject) files.get(0)).getLong("lastModified"));
        auctionUrl.setUrl(((JSONObject) files.get(0)).getString("url"));


        return auctionUrl;
    }


    public static List<Realm> parseRealms(String realmsJsonString) {
        List<Realm> realms = new ArrayList<>();

        if (StringUtils.isEmpty(realmsJsonString))
            return realms;


        JSONObject jsonObject = new JSONObject(realmsJsonString);
        JSONArray jsonRealmsArray = jsonObject.getJSONArray("realms");

        for (int i = 0; i < jsonRealmsArray.length(); i++) {
            JSONObject object = (JSONObject) jsonRealmsArray.get(i);
            Realm realm = new Realm();
            realm.setSlug(object.getString("slug"));
            realm.setName(object.getString("name"));
            JSONArray connectedRealms = object.getJSONArray("connected_realms");
            if (connectedRealms.length() > 0) {
                Set<String> crs = new HashSet<>();
                for (int j = 0; j < connectedRealms.length(); j++) {
                    String r = connectedRealms.getString(j);
                    if (!r.equals(realm.getSlug())) {
                        crs.add(r);
                    }
                }
                realm.setConnectedRealms(crs);
            }

            realms.add(realm);
        }


        return realms;
    }

    
    public static Set<String> lookupRealmConnections(String lookupRealm, List<Realm> realmList) {
        Realm realm =
                realmList.stream().collect(
                        Collectors.toMap(Realm::getSlug, r -> r)).get(lookupRealm);
        if (realm != null) {
            Set<String> connectedRealms = new HashSet<>(realm.getConnectedRealms());
            System.out.println("Connections for the " + realm.getName() + " realm are :");
            connectedRealms.forEach(System.out::println);
            return connectedRealms;
        }
        return Collections.emptySet();
    }


    public static boolean lookupRealmConnections(String realmIn, String lookupRealm, List<Realm> realmList) {
        Realm realm =
                realmList.stream().collect(Collectors.toMap(Realm::getSlug, r -> r)).get(realmIn);
        return realm != null && realm.getConnectedRealms().contains(lookupRealm);

    }


    public static Page<Auction> buildPagedAuctions(List<Auction> auctions, Pageable pageable,
                                                   List<Item> rebornsList) {

        List<Auction> foundAuctions = new ArrayList<>();

        for (Item reborn : rebornsList) {

            for (Auction auction : auctions) {
                //auction.setPpi();
                if (reborn.getItemId() == auction.getItemId()) {
                    auction.setItemId(reborn.getItemId());
                    auction.setOwner(auction.getOwner() + "-" + auction.getOwnerRealm());
                    foundAuctions.add(auction);
                }

            }
        }
        Collections.sort(foundAuctions);
        return new PageImpl<Auction>(foundAuctions, pageable, auctions.size());
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
            //auction.setOwner(obj.getString("owner"));
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

        item.setItemId(obj.getLong("id"));
        item.setName(obj.getString("name"));
        item.setItemLevel(obj.getInt("itemLevel"));
        item.setQuality(obj.getInt("quality"));

        return item;
    }

    public static BlockingQueue<Long> createQueue(List<Long> auctionItems) {
        BlockingQueue<Long> newQueue = new LinkedBlockingQueue<Long>();
        newQueue.addAll(auctionItems);
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
                    reborn.setItemId(obj.getLong("id"));
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
