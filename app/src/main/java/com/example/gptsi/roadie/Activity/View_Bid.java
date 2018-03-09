package com.example.gptsi.roadie.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gptsi.roadie.Adapter.adapter_bid;
import com.example.gptsi.roadie.Pojo.sp_bid;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class View_Bid extends AppCompatActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    adapter_bid adapter;
    ArrayList<sp_bid> mList;
    LinearLayout progressBar;
    TextView noBid;
    shared_preferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bid);

        sp = new shared_preferences(this);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noBid = findViewById(R.id.no_bid);
        toolbar = findViewById(R.id.appbar);

        toolbar.setTitle("View Bid");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mList = new ArrayList<>();
        adapter = new adapter_bid(View_Bid.this, mList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new get_bid().execute();

    }

    class get_bid extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getBid.php?uid="+sp.getId();
        String result;

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(View_Bid.this);
            result = js.processURL(url);

            try {

                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject serviceProvider = data.getJSONObject(i);
                        sp_bid sp = new sp_bid();

                        sp.setId(serviceProvider.getInt("sid"));
                        sp.setPhoto(serviceProvider.getString("photo"));
                        sp.setName(serviceProvider.getString("company_name"));
                        sp.setRating((float) serviceProvider.getDouble("rating"));
                        sp.setReviewCount(serviceProvider.getString("reviewcount"));
                        sp.setPrice(serviceProvider.getInt("bid"));
                        sp.setPhone(serviceProvider.getString("phone"));
                        //TODO sp.setChatId(serviceProvider.getString("chat_id"));
                        mList.add(sp);
                    }

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
            adapter.copyFromOriginal();
            progressBar.setVisibility(View.GONE);
            if(mList.size()>0){
                noBid.setVisibility(View.GONE);
            }
        }
    }
}
