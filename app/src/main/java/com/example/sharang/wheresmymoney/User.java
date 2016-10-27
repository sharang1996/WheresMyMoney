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

    public void calculateBalance()
    {
        double incomeSum=0,expenditureSum=0;

        if(incomes!=null && incomes.size()!=0)
        {
            for (Income i : incomes) {
                incomeSum += i.getAmount();
            }
        }

        if(expenditures!=null && expenditures.size()!=0)
        {
            for (Expenditure i : expenditures) {
                expenditureSum += i.getAmount();
            }
        }
        setBalance(incomeSum-expenditureSum);
    }

    public ArrayList<HistoryItem> getChronologicalEvent() {

        int i=0,j=0;
        ArrayList<HistoryItem> historyItems = new ArrayList<>();
        if( incomes==null || expenditures==null)
        {
            return null;
        }
        while(i < incomes.size() && j < expenditures.size())
        {
            if(incomes.get(i).getTimestamp() < (expenditures.get(j).getTimestamp()))
            {
                historyItems.add(new HistoryItem(incomes.get(i).getAmount(),
                        incomes.get(i).getCategory(),incomes.get(i).getDescription(),
                        incomes.get(i).getTimestamp(),true));
                i++;
            }
            else
            {
                historyItems.add(new HistoryItem(expenditures.get(j).getAmount(),
                        expenditures.get(j).getCategory(),expenditures.get(j).getDescription(),
                        expenditures.get(j).getTimestamp(),false));
                j++;
            }
        }

        if(i < incomes.size())
        {
            while(i < incomes.size())
            {
                historyItems.add(new HistoryItem(incomes.get(i).getAmount(),
                        incomes.get(i).getCategory(),incomes.get(i).getDescription(),
                        incomes.get(i).getTimestamp(),true));
                i++;
            }
        }

        if(j<expenditures.size())
        {
            while(j<expenditures.size())
            {
                historyItems.add(new HistoryItem(expenditures.get(j).getAmount(),
                        expenditures.get(j).getCategory(),expenditures.get(j).getDescription(),
                        expenditures.get(j).getTimestamp(),false));
                j++;
            }
        }

        return historyItems;
    }
}
