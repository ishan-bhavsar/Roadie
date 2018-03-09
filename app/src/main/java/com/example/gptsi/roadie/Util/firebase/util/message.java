package com.example.gptsi.roadie.Util.firebase.util;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.example.gptsi.roadie.Util.util.encryptSHA1;

/**
 * Created by GPTSI on 09-02-2018.
 */

public class message implements Serializable{
    public String title;
    public String regId;
    public String messageId;
    public String message;
    public boolean isBackground;
    public String imageUrl;
    public String timestamp;
    public JSONObject payload;
    public String pushType;

    public message(){
        title = "message";
        regId = "";
        messageId = getUniqueMessageId();
        message = "";
        isBackground = true;
        imageUrl = "";
        payload = new JSONObject();
        pushType = "individual";
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(new Date());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getMessageId() {
        return messageId;
    }

//    public void setMessageId(String messageId) {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(new Date());
//        this.messageId = encryptSHA1(messageId+timeStamp);
//    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isBackground() {
        return isBackground;
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    private String getUniqueMessageId() {
        return "m-" + UUID.randomUUID().toString();
    }
}
