package com.example.gptsi.roadie.Pojo;

import com.example.gptsi.roadie.R;

public class sp_base {

    Integer mId;
    float mRating;
    String mName, mReviewCount, mPhone, mChatId,mPhoto;

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getChatId() {
        return mChatId;
    }

    public void setChatId(String mChatId) {
        this.mChatId = mChatId;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }

    public String getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.mReviewCount = reviewCount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        this.mRating = rating;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer sid) {
        this.mId = sid;
    }
}