package com.example.gptsi.roadie.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Adapter.adapter_add_location;
import com.example.gptsi.roadie.Adapter.adapter_payment_spn;
import com.example.gptsi.roadie.Pojo.payment;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.address_autofill;
import com.example.gptsi.roadie.Util.shared_preferences;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.web.uploadFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Account extends AppCompatActivity {

    Toolbar toolbar;
    CardView personal_detail,payment_detail,location_preferences;
    LinearLayout personal_detail_layout,payment_detail_layout,location_layout;
    TextView personal_text,payment_text,location_text;
    AutoCompleteTextView location;
    ImageView photo,edit,submit,add_location;
    EditText company,name,addr,phone;
    Spinner payment_options;
    RecyclerView locations;
    shared_preferences sp;
    ArrayList<String> mLocations = new ArrayList<>();
    ArrayList<String> mAllCities;
    ArrayAdapter<String> adapter;

    @SuppressLint({"ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        personal_detail = findViewById(R.id.account_personal);
        payment_detail = findViewById(R.id.account_payment);
        location_preferences = findViewById(R.id.account_location);
        personal_detail_layout = findViewById(R.id.account_personal_layout);
        payment_detail_layout = findViewById(R.id.account_payment_layout);
        location_layout = findViewById(R.id.account_location_layout);
        personal_text = findViewById(R.id.account_personal_text);
        payment_text = findViewById(R.id.account_payment_text);
        location_text = findViewById(R.id.account_location_text);
        photo = findViewById(R.id.account_photo);
        location = findViewById(R.id.account_add_location_name);
        add_location = findViewById(R.id.account_add_location);
        company = findViewById(R.id.account_cname);
        name = findViewById(R.id.account_name);
        addr = findViewById(R.id.account_addr);
        phone = findViewById(R.id.account_phone);
        payment_options = findViewById(R.id.account_payment_method);
        locations = findViewById(R.id.account_preferred_location);
        toolbar = findViewById(R.id.appbar);
        edit = toolbar.findViewById(R.id.account_edit);
        submit = toolbar.findViewById(R.id.account_submit);

        edit.setColorFilter(Color.WHITE);
        submit.setColorFilter(Color.WHITE);

        toolbar.setTitle("Account");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        sp = new shared_preferences(Account.this);
        if(sp.getUserType()==shared_preferences.USER)
            location_preferences.setVisibility(View.GONE);

        personal_detail.setTag(false);
        personal_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((boolean) personal_detail.getTag()){
                    personal_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_less,0);
                    personal_detail_layout.setVisibility(View.GONE);
                } else {
                    personal_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more,0);
                    personal_detail_layout.setVisibility(View.VISIBLE);
                }

                personal_detail.setTag(!(boolean) personal_detail.getTag());
            }
        });

        payment_detail.setTag(false);
        payment_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((boolean) payment_detail.getTag()) {
                    payment_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_less,0);
                    payment_detail_layout.setVisibility(View.GONE);
                } else {
                    payment_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more,0);
                    payment_detail_layout.setVisibility(View.VISIBLE);
                }

                payment_detail.setTag(!(boolean) payment_detail.getTag());
            }
        });

        location_preferences.setTag(false);
        location_preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((boolean) location_preferences.getTag()) {
                    location_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_less,0);
                    location_layout.setVisibility(View.GONE);
                } else {
                    location_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_expand_more,0);
                    location_layout.setVisibility(View.VISIBLE);
                }

                location_preferences.setTag(!(boolean) location_preferences.getTag());
            }
        });

        final adapter_add_location adapter_location = new adapter_add_location(Account.this,mLocations);
        locations.setAdapter(adapter_location);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        locations.setLayoutManager(mLayoutManager);
        locations.setItemAnimator(new DefaultItemAnimator());




        fetch_details();




        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocations.add(location.getText().toString());
                adapter_location.notifyDataSetChanged();
                mAllCities.remove(location.getText().toString());
                adapter.notifyDataSetChanged();
                location.setText("");
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                add_location.setVisibility(View.VISIBLE);
                location.setVisibility(View.VISIBLE);
                personal_detail_layout.setVisibility(View.VISIBLE);
                payment_detail_layout.setVisibility(View.VISIBLE);
                location_layout.setVisibility(View.VISIBLE);

                payment_options.setEnabled(true);
                company.setEnabled(true);
                name.setEnabled(true);
                phone.setEnabled(true);
                addr.setEnabled(true);
                adapter_location.manage(true);

                location_preferences.setTag(true);
                personal_detail.setTag(true);
                payment_detail.setTag(true);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                add_location.setVisibility(View.GONE);
                location.setVisibility(View.GONE);

                payment_options.setEnabled(false);
                company.setEnabled(false);
                name.setEnabled(false);
                phone.setEnabled(false);
                addr.setEnabled(false);
                adapter_location.manage(false);
                new update_details().execute();
            }
        });

        address_autofill autofill = new address_autofill(Account.this);
        mAllCities = autofill.getAllCities();
        adapter = new ArrayAdapter<>(Account.this,android.R.layout.simple_spinner_dropdown_item,mAllCities);
        location.setAdapter(adapter);
        location.setThreshold(0);
    }

    void fetch_details(){
        company.setText(sp.getEmail());//TODO company
        name.setText(sp.getName());
        addr.setText(sp.getAddr()+" "+sp.getCity()+" "+sp.getState());
        phone.setText(sp.getEmail()); //TODO phone
        Glide.with(Account.this).load(sp.getPhoto()).into(photo);

        ArrayList<payment> mPaymentList = new ArrayList<>();
        mPaymentList.add(new payment("Cash",R.drawable.ic_money));
        mPaymentList.add(new payment("Paytm",R.drawable.ic_money));
        adapter_payment_spn adapter = new adapter_payment_spn(Account.this,mPaymentList);
        payment_options.setAdapter(adapter);
        payment_options.setEnabled(false);

        mLocations.add("Vadodara");
        mLocations.add("Surat");
    }

    class update_details extends AsyncTask<Void,Void,Void> {
        String result, url;
        ProgressDialog pd;
        Json js;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Account.this);
            pd.setTitle("Updating In...");
            pd.setMessage("Please Wait");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(Account.this);
            result = js.processURL(url);
            uploadFile file = new uploadFile();

//                photo = file.upload(photo,false);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pd.isShowing()) {
                pd.dismiss();
            }

            if (result.length() < 3) {
                Toast.makeText(Account.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Account.this, "Update successful", Toast.LENGTH_SHORT).show();

//TODO                sp.setPhoto();
                sp.setName(name.getText().toString());
                sp.setAddr(addr.getText().toString());
                sp.setNum(phone.getText().toString());

            }
        }
    }
}
