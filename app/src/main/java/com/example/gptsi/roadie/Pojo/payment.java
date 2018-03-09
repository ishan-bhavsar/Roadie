package com.example.gptsi.roadie.Pojo;

/**
 * Created by GPTSI on 31-01-2018.
 */
public class payment {

    String mName;
    int mImage;

    public payment(String mName, int mImage) {
        this.mName = mName;
        this.mImage = mImage;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }
}
