package com.example.localist.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemModel implements Serializable {
    private String id;
    private String title;
    private String address;
    private String description;
    private double score;
    private String wikiUrl;
    private String bookingUrl;
    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    private ArrayList<String> pic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public ArrayList<String> getPic() {
        return pic;
    }

    public void setPic(ArrayList<String> pic) {
        this.pic = pic;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public ItemModel(){

    }


}
