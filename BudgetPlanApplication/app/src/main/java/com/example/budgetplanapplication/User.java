package com.example.budgetplanapplication;


import java.io.Serializable;

public class User implements Serializable {

    private String userUID;
    private String userincome;
    private String additionalincome;

    public User() {
    }

    public User(String userUID, String userincome) {

        this.userUID = userUID;
        this.userincome = userincome;
    }



    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserincome(){ return userincome; }

    public void setUserincome(String userincome){this.userincome = userincome;}
}
