package com.example.gptsi.roadie.Pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ishan Bhavsar on 05-03-2018.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}