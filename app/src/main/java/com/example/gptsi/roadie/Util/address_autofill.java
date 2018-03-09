package com.example.gptsi.roadie.Util;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GPTSI on 12-02-2018.
 */

public class address_autofill {

    Context mContext;
    ArrayList<String> mCity;
    ArrayList<String> mAllCities;
    ArrayList<String> mState;
    HashMap<String,ArrayList<String>> mList;
    public static final String ALL_CITIES = "ALL";

    public address_autofill(Context context){
        mContext = context;
        mCity = new ArrayList<>();
        mAllCities = new ArrayList<>();
        mState = new ArrayList<>();
        mList = new HashMap<>();
        parse();
    }

    private String loadJSONFromAsset(String file) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public void parse() {

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("city.json"));
            JSONArray data = obj.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject state = data.getJSONObject(i);
                String s = String.valueOf((state.names()).get(0));
                mState.add(s);
                Log.d("Details-->", s);

                JSONArray city = state.getJSONArray(s);

                for (int j = 0; j < city.length(); j++) {
                    JSONObject c = city.getJSONObject(j);
                    String scity = c.getString("city");
                    Log.d("Details------->", scity);
                    mAllCities.add(scity);
                    mCity.add(scity);
                }
                mList.put(s,mCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllCities() {
        return mAllCities;
    }

    public ArrayList<String> getCity(String state) {
        if(state.equals(ALL_CITIES))
            return mAllCities;
        else
            return mList.get(state);
    }

    public ArrayList<String> getState() {
        return mState;
    }


    public ArrayAdapter<String> getCityAdapter(String state) {
        ArrayAdapter<String> adapter;
        if(state.equals(ALL_CITIES))
            adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_dropdown_item,mAllCities);
        else
            adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_dropdown_item,mList.get(state));

        return adapter;
    }

    public ArrayAdapter<String> getStateAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_spinner_dropdown_item,mState);
        return adapter;
    }

}
