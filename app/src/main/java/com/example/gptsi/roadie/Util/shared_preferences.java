package com.example.gptsi.roadie.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.gptsi.roadie.Util.globals.MAX_REM_USERS;
import static com.example.gptsi.roadie.Util.globals.SHARED_PREFERENCE_FILE;

/**
 * Created by GPTSI on 03-12-2017.
 */

public class shared_preferences extends AppCompatActivity {
    SharedPreferences mSharedPref;
    SharedPreferences.Editor mEditor;
    ArrayList<String> mUsers;
    public final static int USER = 0;
    public final static int SERVICEPROVIDER = 1;

    public shared_preferences(Context context)
    {
        mSharedPref = context.getSharedPreferences(SHARED_PREFERENCE_FILE,MODE_PRIVATE);
        mEditor = mSharedPref.edit();
        mUsers = new ArrayList<>();
    }

    public boolean isRegUser()
    {
        return mSharedPref.contains("name");
    }

    public List getRegUsers()
    {
        mUsers.clear();
        String s;
        for(int i=1;i<=MAX_REM_USERS;i++) {
            s = mSharedPref.getString(Integer.toString(i),"");
            if (s.length()!=0) {
                mUsers.add(s);
            } else {
                break;
            }
        }
        return mUsers;
    }

    public void addUser(String email)
    {
        String s;
        int j;
        boolean alreadyReg = false;
        for(j=1;j<=MAX_REM_USERS;j++) {
            s = mSharedPref.getString(Integer.toString(j), "");
            if (s.length()==0) {
                break;
            } else if(s.equals(email)) {
                alreadyReg = true;
            }
        }
        if(!alreadyReg) {
            mEditor.putString(Integer.toString(j), email);
        }
    }

    public void clearPreferences()
    {
        String s = mSharedPref.getString(Integer.toString(MAX_REM_USERS),"");
        String semail = mSharedPref.getString("email","");
        int remId = mSharedPref.getInt("remId",0);
        if(s.length()!=0) {
            mEditor.clear();
        } else {
            mEditor.remove("id");
            mEditor.remove("name");
            mEditor.remove("email");
            mEditor.remove("addr");
            mEditor.remove("city");
            mEditor.remove("state");
            mEditor.remove("phone");
            mEditor.remove("pass");
            mEditor.remove("photo");
        }
        if(remId == 1)
        {
            mEditor.putInt("remId",remId);
            mEditor.putString("email",semail);
        }
        mEditor.commit();
    }

    public String getId() {
        return mSharedPref.getString("id", "");
    }

    public void setId(String id) {
        mEditor.putString("id",id);
        mEditor.commit();
    }

    public int getUserType() {
        Log.d("SharedPref: ",mSharedPref.contains("usrType")+" utype "+mSharedPref.getInt("usrType", 0));
        return mSharedPref.getInt("usrType", 0) == SERVICEPROVIDER ? SERVICEPROVIDER : USER ;
    }

    public void setUserType(int userType) {
        mEditor.putInt("usrType",userType);
        mEditor.commit();
    }

    public String getName() {
        return mSharedPref.getString("name", "");
    }

    public void setName(String name) {
        mEditor.putString("name",name);
        mEditor.commit();
    }

    public String getAddr() {
        return mSharedPref.getString("addr", "");
    }

    public void setAddr(String addr) {
        mEditor.putString("addr",addr);
        mEditor.commit();
    }

    public String getCity() {
        return mSharedPref.getString("city", "");
    }

    public void setCity(String city) {
        mEditor.putString("city",city);
        mEditor.commit();
    }

    public String getState() {
        return mSharedPref.getString("state", "");
    }

    public void setState(String state) {
        mEditor.putString("state",state);
        mEditor.commit();
    }

    public String getNum() {
        return mSharedPref.getString("phone", "");
    }

    public void setNum(String num) {
        mEditor.putString("phone",num);
        mEditor.commit();
    }

    public String getEmail() {
        return mSharedPref.getString("email", "");
    }

    public void setEmail(String email) {
        mEditor.putString("email",email);
        mEditor.commit();
    }

    public String getPass() {
        return mSharedPref.getString("pass", "");
    }

    public void setPass(String pass) {
        mEditor.putString("pass",pass);
        mEditor.commit();
    }

    public String getPhoto() {
        return mSharedPref.getString("photo","");
    }

    public void setPhoto(String photo) {
        mEditor.putString("photo",photo);
        mEditor.commit();
    }

    public Integer getRemId() {
        return mSharedPref.getInt("remId", 0);
    }

    public void setRemId(Integer remId) {
        mEditor.putInt("remId",remId);
        mEditor.commit();
    }

}
