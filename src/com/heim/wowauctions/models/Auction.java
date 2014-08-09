package com.heim.wowauctions.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */


public class Auction {
    public String getDate() {
        return new Date(this.timestamp).toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public interface BaseView {}


    @JsonView(BaseView.class)
    private long auc;

    private long item;
    private String itemName;
    private long itemLevel;
    private String owner;

    @JsonView(BaseView.class)
    private String ownerRealm;

    private long bid;
    private long buyout;
    private int quantity;
    private String timeLeft;
    @JsonView(BaseView.class)
    private int rand;
    @JsonView(BaseView.class)
    private long seed;


    private Date date;
    private long timestamp;


    public long getAuc() {
        return auc;
    }

    public void setAuc(Long auc) {
        this.auc = auc;
    }

    public long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerRealm() {
        return ownerRealm;
    }

    public void setOwnerRealm(String ownerRealm) {
        this.ownerRealm = ownerRealm;
    }

    public String getBid() {
        return buildPrice(this.bid);
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getBuyout() {
        return buildPrice(this.buyout);
    }

    public void setBuyout(Long buyout) {
        this.buyout = buyout;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }



    public int getRand() {
        return rand;
    }

    public void setRand(int rand) {
        this.rand = rand;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    private String buildPrice(long price){
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



    public String toString(){
        StringBuilder sb = new StringBuilder();
    //    {"buyout":19000,"ownerRealm":"Nazgrel","seed":1961897600,"auc":1007454001,"item":76097,"owner":"Tututeri","timeLeft":"VERY_LONG","quantity":1,"rand":0,"bid":17000}
                 sb.append("{ auc: ").append(this.getAuc()).
                append(", owner: ").append(this.getOwner()).
                append("-").append(this.getOwnerRealm()).
                append(", itemName: ").append(this.getItemName()).
                append(", itemLevel: ").append(this.getItemLevel()).
                append(", bid: ").append(this.getBid()).
                append(", buyout: ").append(this.getBuyout()).
                append(", timeleft: ").append(this.getTimeLeft())
                .append("}");

        return sb.toString();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(long itemLevel) {
        this.itemLevel = itemLevel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
