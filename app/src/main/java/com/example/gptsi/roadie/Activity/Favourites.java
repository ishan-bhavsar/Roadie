package com.example.gptsi.roadie.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gptsi.roadie.Adapter.adapter_fav;
import com.example.gptsi.roadie.Pojo.sp_base;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class Favourites extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    adapter_fav adapter;
    ArrayList<sp_base> mList;
    LinearLayout progressBar;
    TextView noFav;
    shared_preferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        sp = new shared_preferences(this);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noFav = findViewById(R.id.no_favourites);
        toolbar = findViewById(R.id.appbar);

        toolbar.setTitle("Favourites");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mList = new ArrayList<>();
        adapter = new adapter_fav(Favourites.this, mList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new get_favourites().execute();

        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        navigation.setVisibility(View.GONE);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_delete:
                    adapter.deleteFavourites();
                    return true;
            }
            return false;
        }
    };

    class get_favourites extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getFavourites.php?uid="+sp.getId();
        String result;

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(Favourites.this);
            result = js.processURL(url);

            try {

                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject serviceProvider = data.getJSONObject(i);
                        sp_base sp = new sp_base();

                        sp.setId(serviceProvider.getInt("sid"));
                        sp.setPhoto(serviceProvider.getString("photo"));
                        sp.setName(serviceProvider.getString("company_name"));
                        sp.setRating((float) serviceProvider.getDouble("rating"));
                        sp.setReviewCount(serviceProvider.getString("reviewcount"));
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
                noFav.setVisibility(View.GONE);
            }
        }
    }
}
