package com.example.gptsi.roadie.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.address_autofill;

import static android.content.Context.MODE_PRIVATE;

public class Address {

    Context mContext;
    View mView;
    TextView title, floors_label;
    EditText addr, floors;
    AutoCompleteTextView city, state;
    Switch lift, loading;
    LinearLayout liftlayout;
    Spinner house, bedrooms;
    Toolbar toolbar;
    String list_H[] = {"House Type", "Apartment", "Bungalow", "House"};
    String list_B[] = {"Bedrooms", "1BHK", "2BHK", "3BHK", "4BHK", "5BHK"};
    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    String saddr, scity, sstate, location, shouse, sbedrooms, sfloors, shouse_descr;
    Integer bLift = 0, bLoading = 0;
    ArrayAdapter<String> adapter;

    public Address(Context context, View view, Boolean isSourceAddress) {

        this.mContext = context;
        this.mView = view;

        title = mView.findViewById(R.id.addr_title);
        addr = mView.findViewById(R.id.addr_addr);
        city = mView.findViewById(R.id.addr_city);
        state = mView.findViewById(R.id.addr_state);
        lift = mView.findViewById(R.id.addr_lift);
        loading = mView.findViewById(R.id.addr_loading);
        house = mView.findViewById(R.id.addr_house);
        bedrooms = mView.findViewById(R.id.addr_bedrooms);
        floors_label = mView.findViewById(R.id.addr_floor_label);
        floors = mView.findViewById(R.id.addr_floor);
        liftlayout = mView.findViewById(R.id.addr_lift_layout);
        toolbar = mView.findViewById(R.id.appbar);

        toolbar.setTitle("Address");
//TODO        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Address.this,Add_Request.class));
//                finish();
//            }
//        });



        final address_autofill autofill = new address_autofill(mContext);
        state.setAdapter(autofill.getStateAdapter());
        state.setThreshold(0);

        state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                city.setAdapter(autofill.getCityAdapter(state.getText().toString()));
                city.setThreshold(0);
            }
        });


        mPref = mContext.getSharedPreferences("AddRequest", MODE_PRIVATE);
        editor = mPref.edit();

        location = isSourceAddress ? "Source" : "Destination";
        title.setText(location);

        lift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bLift = isChecked ? 1 : 0;
            }
        });

        loading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bLoading = isChecked ? 1 : 0;
            }
        });

        floors.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sfloors = location + "_floors" + floors.getText().toString();
            }
        });

        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, list_H);
        house.setAdapter(adapter);
        house.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shouse = list_H[position];
                if ("Apartment".equals(list_H[position])) {
                    floors_label.setVisibility(View.VISIBLE);
                    floors.setVisibility(View.VISIBLE);
                    liftlayout.setVisibility(View.VISIBLE);
                } else {
                    floors_label.setVisibility(View.GONE);
                    floors.setVisibility(View.GONE);
                    liftlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, list_B);
        bedrooms.setAdapter(adapter);
        bedrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sbedrooms = list_B[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fetch_data();
    }

    void fetch_data() {
        if (mPref.contains(location)) {

            addr.setText(mPref.getString(location + "_addr", saddr));
            city.setText(mPref.getString(location + "_city", scity));
            state.setText(mPref.getString(location + "_state", sstate));
            editor.putString(location + "_house_descr", shouse_descr);
            lift.setChecked(mPref.getString(location + "_lift", "").equals("1"));
            loading.setChecked(mPref.getString(location + "_loading", "").equals("1"));
        }
    }

    public Boolean submit() {

        saddr = addr.getText().toString();
        scity = city.getText().toString();
        sstate = state.getText().toString();
        shouse_descr = location + "_house" + shouse + " " +
                location + "_bedrooms" + sbedrooms + " ";
        if (sfloors != null)
            shouse_descr += sfloors;

        if (saddr.equals("") || scity.equals("") || sstate.equals("") || shouse.equals("") || sbedrooms.equals("")) {
            Toast.makeText(mContext, "Fill all the fields", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            editor.putString(location + "_addr", saddr);
            editor.putString(location + "_city", scity);
            editor.putString(location + "_state", sstate);
            editor.putString(location + "_house_descr", shouse_descr);
            editor.putString(location + "_lift", "" + bLift);
            editor.putString(location + "_loading", "" + bLoading);
            editor.putString(location, "Complete");
            editor.commit();
            return true;
        }
    }
}
