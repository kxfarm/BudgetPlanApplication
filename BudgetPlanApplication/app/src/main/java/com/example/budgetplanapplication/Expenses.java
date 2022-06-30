package com.example.budgetplanapplication;

public class Expenses {
    private String expensesname;
    private String userUID;
    private String uuid;

    public Expenses(String expensesname, String userUID, String uuid) {
        this.expensesname = expensesname;
        this.userUID = userUID;
        this.uuid = uuid;
    }

    public Expenses(String expensesname) {
        this.expensesname = expensesname;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getExpensesname() {
        return expensesname;
    }

    public void setExpensesname(String expensesname) {
        this.expensesname = expensesname;
    }
}

