package com.hotel.hotelbooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    private int roomNumber;

    private String category;
    private String bedType;
    private int capacity;
    private double price;
    private boolean spa;
    private boolean pool;
    private boolean balcony;
    private boolean available;

    public Room() {
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getBedType() {
        return bedType;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSpa() {
        return spa;
    }

    public boolean isPool() {
        return pool;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}