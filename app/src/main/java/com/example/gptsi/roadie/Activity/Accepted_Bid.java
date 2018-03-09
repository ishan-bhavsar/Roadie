package com.example.gptsi.roadie.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;
import static com.example.gptsi.roadie.Util.globals.SHARED_PREFERENCE_FILE;

public class Accepted_Bid extends AppCompatActivity {

    Toolbar toolbar;
    TextView mSrc,mDst,mDate,mPrice,mName,addr,city,state;
    ImageView mPhoto;
    Button Track;
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_bid);

        mSrc=(TextView)findViewById(R.id.accept_from);
        mDst=(TextView)findViewById(R.id.accept_to);
        mDate=(TextView)findViewById(R.id.accept_date);
        mPrice=(TextView)findViewById(R.id.accept_price);
        mName=(TextView)findViewById(R.id.accept_name);
        mPhoto=(ImageView)findViewById(R.id.accept_photo);
        Track=(Button)findViewById(R.id.accept_track);
        toolbar=(Toolbar)findViewById(R.id.appbar);
        toolbar.setTitle("Home");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mPref = getSharedPreferences(SHARED_PREFERENCE_FILE,MODE_PRIVATE);
        mEditor = mPref.edit();

        mDialog = new Dialog(Accepted_Bid.this);
        mDialog.setContentView(R.layout.single_accept_dialog);
        mDialog.setTitle("Address");

        mSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addr= mDialog.findViewById(R.id.accept_dialog_addr);
                city= mDialog.findViewById(R.id.accept_dialog_city);
                state= mDialog.findViewById(R.id.accept_dialog_state);
                addr.setText(mPref.getString("s_addr",""));
                city.setText(mPref.getString("s_city",""));
                state.setText(mPref.getString("s_state",""));
                mDialog.show();
            }
        });

        mDst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addr= mDialog.findViewById(R.id.accept_dialog_addr);
                city= mDialog.findViewById(R.id.accept_dialog_city);
                state= mDialog.findViewById(R.id.accept_dialog_state);
                addr.setText(mPref.getString("d_addr",""));
                city.setText(mPref.getString("d_city",""));
                state.setText(mPref.getString("d_state",""));
                mDialog.show();
            }
        });

        Track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Accepted_Bid.this,Track_ServiceProvider.class));
            }
        });

        new get_info().execute();
    }

    class get_info extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getInfo.php?uid="+mPref.getString("uid","1")+"&sid="+mPref.getString("sid","1");
        String result;

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(Accepted_Bid.this);
            result = js.processURL(url);

            try {

                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");
                    JSONObject info = data.getJSONObject(0);

                    mEditor.putString("s_addr",info.getString("s_addr"));
                    mEditor.putString("s_city",info.getString("s_city"));
                    mEditor.putString("s_state",info.getString("s_state"));
                    mEditor.putString("d_addr",info.getString("d_addr"));
                    mEditor.putString("d_city",info.getString("d_city"));
                    mEditor.putString("d_state",info.getString("d_state"));
                    mEditor.putString("date",info.getString("date"));
                    mEditor.putString("time",info.getString("time"));
                    mEditor.putString("bid",info.getString("bid"));
                    mEditor.putString("name",info.getString("company_name"));
                    mEditor.putString("photo",info.getString("photo"));
                    mEditor.commit();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mSrc.setText(mPref.getString("s_city",""));
            mDst.setText(mPref.getString("d_city",""));
            mDate.setText(mPref.getString("date",""));
            mPrice.setText(mPref.getString("bid",""));
            mName.setText(mPref.getString("name",""));
            //TODO mPhoto.setText(mPref.getString("photo",""));

        }
    }
}
