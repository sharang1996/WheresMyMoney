package com.example.sharang.wheresmymoney;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by sharang on 15/9/16.
 */

public class User {

    private String uid;
    private String name;
    private String email;
    private String password;
    private double balance;
    ArrayList<Income> incomes;
    ArrayList<Expenditure> expenditures;

    public User()
    {
      //Empty Constructor needed for serialization
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<Expenditure> getExpenditures() {
        return expenditures;
    }

    public void setExpenditures(ArrayList<Expenditure> expenditures) {
        this.expenditures = expenditures;
    }

    public ArrayList<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ArrayList<Income> incomes) {
        this.incomes = incomes;
    }

    public void addIncome(Income income){
        if(incomes==null) incomes = new ArrayList<>();
        incomes.add(income);
    }

    public void addExpenditure(Expenditure expenditure) {
        if(expenditures==null) expenditures = new ArrayList<>();
        expenditures.add(expenditure);
    }
}
