package com.example.budgetplanapplication;

public class Saving {
    private String savingname;
    private String savingamount;
    private String targetdate;
    private String userUID;
    private String uuid;
    private String savingpermonth;
    private String targetmonth;
    private String cumulativesaving;
    private String cumulativemonth;


    public Saving(String savingname, String savingamount, String targetdate, String userUID, String uuid, String savingpermonth, String targetmonth, String cumulativesaving, String cumulativemonth) {
        this.savingname = savingname;
        this.savingamount = savingamount;
        this.targetdate = targetdate;
        this.userUID = userUID;
        this.uuid = uuid;
        this.savingpermonth = savingpermonth;
        this.targetmonth = targetmonth;
        this.cumulativesaving = cumulativesaving;
        this.cumulativemonth = cumulativemonth;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTargetmonth(String targetmonth) {
        this.targetmonth = targetmonth;
    }

    public void setCumulativesaving(String cumulativesaving) {
        this.cumulativesaving = cumulativesaving;
    }

    public String getCumulativemonth() {
        return cumulativemonth;
    }

    public void setCumulativemonth(String cumulativemonth) {
        this.cumulativemonth = cumulativemonth;
    }

    public String getSavingpermonth() {
        return savingpermonth;
    }

    public void setSavingpermonth(String savingpermonth) {
        this.savingpermonth = savingpermonth;
    }

    public String getTargetmonth() {
        return targetmonth;
    }

    public String getCumulativesaving() {
        return cumulativesaving;
    }


    public String getUuid() {
        return uuid;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getSavingname() {
        return savingname;
    }

    public void setSavingname(String savingname) {
        this.savingname = savingname;
    }

    public String getSavingamount() {
        return savingamount;
    }

    public void setSavingamount(String savingamount) {
        this.savingamount = savingamount;
    }

    public String getTargetdate() {
        return targetdate;
    }

    public void setTargetdate(String targetdate) {
        this.targetdate = targetdate;
    }
}
