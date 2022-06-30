package com.example.budgetplanapplication;

public class UserExpenses {
    private String userUID;
    private String expensescategories;
    private String expensesamount;
    private String expensesdate;
    private String expensesdescription;
    private String barid;
    private String productdescription;

    public UserExpenses(String userUID, String expensescategories, String expensesamount, String expensesdate, String expensesdescription, String barid, String productdescription) {
        this.userUID = userUID;
        this.expensescategories = expensescategories;
        this.expensesamount = expensesamount;
        this.expensesdate = expensesdate;
        this.expensesdescription = expensesdescription;
        this.barid = barid;
        this.productdescription = productdescription;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public void setProductdescription(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getBarid() {
        return barid;
    }

    public void setBarid(String barid) {
        this.barid = barid;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getExpensescategories() {
        return expensescategories;
    }

    public void setExpensescategories(String expensescategories) {
        this.expensescategories = expensescategories;
    }

    public String getExpensesamount() {
        return expensesamount;
    }

    public void setExpensesamount(String expensesamount) {
        this.expensesamount = expensesamount;
    }

    public String getExpensesdate() {
        return expensesdate;
    }

    public void setExpensesdate(String expensesdate) {
        this.expensesdate = expensesdate;
    }

    public String getExpensesdescription() {
        return expensesdescription;
    }

    public void setExpensesdescription(String expensesdescription) {
        this.expensesdescription = expensesdescription;
    }
}
