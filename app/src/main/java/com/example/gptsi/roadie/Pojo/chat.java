package com.example.gptsi.roadie.Pojo;

import android.net.Uri;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by GPTSI on 18-01-2018.
 */

@IgnoreExtraProperties
public class chat {

    String sender;
    String receiver;
    String msg;
    String image;
    String caption;
    int media;

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int LOCATION = 2;

    public chat() {
        this.sender = "";
        this.receiver = "";
        this.msg = "";
        this.caption = "";
        this.media = TEXT;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int mMedia) {
        this.media = mMedia;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String mSender) {
        this.sender = mSender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String mReceiver) {
        this.receiver = mReceiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String mMsg) {
        this.msg = mMsg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String mImage) {
        this.image = mImage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String mCaption) {
        this.caption = mCaption;
    }
}
