package com.example.gptsi.roadie.Pojo;

import java.util.ArrayList;

/**
 * Created by GPTSI on 13-12-2017.
 */

public class usr_history {
    String mTxDate;
    String mPrice;
    String mSrc, mDst, mName;
    ArrayList<String> mItems;

    public String getTxDate() {
        return mTxDate;
    }

    public void setTxDate(String txDate) {
        mTxDate = txDate;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getSrc() {
        return mSrc;
    }

    public void setSrc(String src) {
        mSrc = src;
    }

    public String getDst() {
        return mDst;
    }

    public void setDst(String dst) {
        mDst = dst;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<String> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<String> items) {
        mItems = items;
    }
}
