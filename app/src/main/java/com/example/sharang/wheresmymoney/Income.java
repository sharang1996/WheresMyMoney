package com.example.sharang.wheresmymoney;

/**
 * Created by sharang on 15/9/16.
 */

public class Income {

    private double amount;
    private String category;
    private String description;

    public Income()
    {
        //empty constructor for serialization
    }

    public Income(String category) {
        this.category = category;
    }

    public Income(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
