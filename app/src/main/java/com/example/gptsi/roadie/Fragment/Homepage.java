package com.example.gptsi.roadie.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.gptsi.roadie.Adapter.adapter_sp_base;
import com.example.gptsi.roadie.Pojo.sp_base;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 07-12-2017.
 */

public class Homepage extends android.support.v4.app.Fragment {

    RecyclerView recyclerView;
    public adapter_sp_base adapter;
    ArrayList<sp_base> mList;
    LinearLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home,null);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);

        mList = new ArrayList<>();
        adapter = new adapter_sp_base(getActivity(),mList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new get_serviceProviders().execute();

        return view;
    }

    class get_serviceProviders extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getServiceProviders.php?city=*";
        String result;

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(getActivity());
            result = js.processURL(url);

            try {

                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject sp = data.getJSONObject(i);
                        sp_base serviceProvider = new sp_base();

                        serviceProvider.setId(sp.getInt("sid"));
                        serviceProvider.setName(sp.getString("company_name"));
                        serviceProvider.setPhoto(sp.getString("photo"));
                        serviceProvider.setRating((float) sp.getDouble("rating"));
                        serviceProvider.setReviewCount(sp.getString("reviewcount"));
                        mList.add(serviceProvider);
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
        }
    }
}
