package com.example.gptsi.roadie.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gptsi.roadie.Adapter.adapter_history;
import com.example.gptsi.roadie.Pojo.usr_history;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class History extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    adapter_history adapter;
    ArrayList<usr_history> mList;
    LinearLayout progressBar;
    TextView noHistory;
    shared_preferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sp = new shared_preferences(this);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noHistory = findViewById(R.id.no_history);
        toolbar = findViewById(R.id.appbar);

        toolbar.setTitle("History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mList = new ArrayList<>();
        adapter = new adapter_history(History.this, mList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new get_history().execute();
    }

    class get_history extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getHistory.php?uid="+sp.getId();
        String result;
        shared_preferences preferences;

        public get_history() {
            preferences = new shared_preferences(History.this);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(History.this);
            result = js.processURL(url);

            try {

                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    final JSONArray data = res.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject serviceProvider = data.getJSONObject(i);
                        usr_history sp = new usr_history();

                        sp.setTxDate(serviceProvider.getString("date"));
                        sp.setSrc(serviceProvider.getString("s_city"));
                        sp.setDst(serviceProvider.getString("d_city"));
                        sp.setPrice(serviceProvider.getString("bid"));

                        if(preferences.getUserType()==shared_preferences.USER)
                            sp.setName(serviceProvider.getString("name"));
                        else
                            sp.setName(serviceProvider.getString("company_name"));

                        sp.setItems(addImages());

                        mList.add(sp);
                    }

                    Comparator<usr_history> comparator = new Comparator<usr_history>(){

                        @Override
                        public int compare(usr_history h1, usr_history h2) {
                            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                return date.parse(h1.getTxDate()).before(date.parse(h2.getTxDate()))?1:-1;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    };

                    Collections.sort(mList,comparator);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            if(mList.size()>0){
                noHistory.setVisibility(View.GONE);
            }
        }
    }

    ArrayList<String> addImages(){

        ArrayList<String> mItems = new ArrayList<>();

        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/1.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/2.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/3.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/4.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/5.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/6.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/7.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/8.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/9.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/10.jpg");
        mItems.add("https://ishan-bhavsar.000webhostapp.com/Roadie/Uploads/11.jpg");
        return mItems;
    }
}
