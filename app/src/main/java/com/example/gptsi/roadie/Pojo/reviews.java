package com.example.gptsi.roadie.Pojo;

import com.example.gptsi.roadie.R;

/**
 * Created by GPTSI on 02-12-2017.
 */

public class reviews{

    String mPhoto;
    String mUser, mReview;
    Float mRating;

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        this.mUser = user;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        this.mReview = review;
    }

    public Float getRating() {
        return mRating;
    }

    public void setRating(Float rating) {
        this.mRating = rating;
    }
}
