package com.example.gptsi.roadie.Util.web;

import android.content.Context;

import com.example.gptsi.roadie.Util.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class Json {

    public static String SUCCESS = "success";  // return this on successful execution of query
    String mResult;
    Boolean mConnected=false;
    Context mContext;

    public Json(Context context) {
        this.mContext = context;
    }

    public String  processURL(String s) {

        mConnected= util.checkInternetConnection(mContext);

        if (mConnected) {

            try {
                URL url = new URL(s);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                int code = conn.getResponseCode();

                if (code == 200) {
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    try {
                        mResult = sb.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mResult;
        }
        return "null";//TODO put null check in all AsyncTasks
    }
}