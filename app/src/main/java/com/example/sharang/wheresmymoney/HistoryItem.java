package com.example.sharang.wheresmymoney;

import android.os.Parcel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sharang on 24/10/16.
 */
public class HistoryItem implements Serializable {

    final static long serialVersionUID = 0l;

    private double amount;
    private String category;
    private String description;
    private long timestamp;
    private boolean isIncome;

    public HistoryItem(double amount, String category, String description, long timestamp,boolean isIncome) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.timestamp = timestamp;
        this.isIncome = isIncome;
    }

    protected HistoryItem(Parcel in) {
        amount = in.readDouble();
        category = in.readString();
        description = in.readString();
        timestamp = in.readLong();
        isIncome = in.readByte() != 0;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isIncome() { return isIncome; }

    public void setIncome(boolean income) { isIncome = income; }

    public String toString(){
        StringBuffer s = new StringBuffer();
        if(this.isIncome()) s.append("INCOME : \t \n");
        else s.append("EXPENDITURE : \t \n");
        s.append("CATEGORY :\t" +this.getCategory()+"\n");
        s.append("AMOUNT :\t" +this.getAmount()+"\n");
        s.append("DESCRIPTION :\t" +this.getDescription()+"\n");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
        Calendar c    = Calendar.getInstance();
        c.setTimeInMillis(this.getTimestamp());
        Date day      = c.getTime();

        s.append("DATE AND TIME :\t" +df.format(day)+"\n\n");

        return s.toString();
    }
}
