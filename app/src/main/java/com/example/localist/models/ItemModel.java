package com.example.localist.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.localist.database.ListTypeConverter;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "saved_items")
public class ItemModel implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String address;
    private String description;
    private double score;
    private String wikiUrl;
    private String bookingUrl;

    @TypeConverters(ListTypeConverter.class)
    private ArrayList<String> pic;

    public ItemModel() {
        // Required by Room and Firebase
    }

    // Getters and setters

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public ArrayList<String> getPic() {
        return pic;
    }

    public void setPic(ArrayList<String> pic) {
        this.pic = pic;
    }
}
