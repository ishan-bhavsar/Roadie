package com.example.gptsi.roadie.Pojo;

import java.util.ArrayList;

/**
 * Created by GPTSI on 27-01-2018.
 */

public class request {

    String mScity,mDcity,mSaddr,mDaddr,mSHtype,mDHtype,
            mSbedroom,mDbedroom,mSlift,mDlift;

    ArrayList<String> mItems;

    String mBid,mDate,mLoading;


    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getScity() {
        return mScity;
    }

    public void setScity(String mScity) {
        this.mScity = mScity;
    }

    public String getDcity() {
        return mDcity;
    }

    public void setDcity(String mDcity) {
        this.mDcity = mDcity;
    }

    public String getSaddr() {
        return mSaddr;
    }

    public void setSaddr(String mSaddr) {
        this.mSaddr = mSaddr;
    }

    public String getDaddr() {
        return mDaddr;
    }

    public void setDaddr(String mDaddr) {
        this.mDaddr = mDaddr;
    }

    public String getSHtype() {
        return mSHtype;
    }

    public void setSHtype(String mSHtype) {
        this.mSHtype = mSHtype;
    }

    public String getDHtype() {
        return mDHtype;
    }

    public void setDHtype(String mDHtype) {
        this.mDHtype = mDHtype;
    }

    public String getSbedroom() {
        return mSbedroom;
    }

    public void setSbedroom(String mSbedroom) {
        this.mSbedroom = mSbedroom;
    }

    public String getDbedroom() {
        return mDbedroom;
    }

    public void setDbedroom(String mDbedroom) {
        this.mDbedroom = mDbedroom;
    }

    public String getLoading() {
        return mLoading;
    }

    public void setLoading(String mLoading) {
        this.mLoading = mLoading;
    }

    public String getSlift() {
        return mSlift;
    }

    public void setSlift(String mSlift) {
        this.mSlift = mSlift;
    }

    public String getDlift() {
        return mDlift;
    }

    public void setDlift(String mDlift) {
        this.mDlift = mDlift;
    }

    public ArrayList<String> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<String> mItems) {
        this.mItems = mItems;
    }

    public String getBid() {
        return mBid;
    }

    public void setBid(String mBid) {
        this.mBid = mBid;
    }
}
