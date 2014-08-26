package com.heim.wowauctions.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.heim.wowauctions.utils.AuctionUtils;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */


public class Auction implements Comparable<Auction> {
    public String getDate() {
        return new Date(this.timestamp).toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }




    public interface BaseView {}


    @JsonView(BaseView.class)
    private long auc;

    private Item item;

    @JsonView(BaseView.class)
    private long itemId;

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
        return AuctionUtils.buildPrice(this.bid);
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getBuyout() {
        return AuctionUtils.buildPrice(this.buyout);
    }

    public long getLongBuyout(){
        return this.buyout;
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




    public String toString(){
        StringBuilder sb = new StringBuilder();
                sb.append("{ auc: ").append(this.getAuc()).
                append(", owner: ").append(this.getOwner()).
                append("-").append(this.getOwnerRealm()).
                append(", bid: ").append(this.getBid()).
                append(", buyout: ").append(this.getBuyout()).
                append(", timeleft: ").append(this.getTimeLeft())
                .append("}");

        return sb.toString();
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(Auction compareAiction) {

        long compareQuantity = compareAiction.getLongBuyout();

        //ascending order
        //return this.quantity - compareQuantity;

        //descending order
        return (int)(compareQuantity - this.buyout);

    }


}
