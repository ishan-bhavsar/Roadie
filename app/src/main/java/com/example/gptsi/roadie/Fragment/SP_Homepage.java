package com.example.gptsi.roadie.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.gptsi.roadie.Adapter.adapter_request;
import com.example.gptsi.roadie.Pojo.request;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 27-01-2018.
 */

public class SP_Homepage extends Fragment {

    RecyclerView recyclerView;
    adapter_request adapter;
    ArrayList<request> mList;
    LinearLayout progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home,null);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);

        mList = new ArrayList<>();
        adapter = new adapter_request(getActivity(),mList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new get_requests().execute();

        return view;
    }

    class get_requests extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS+"getRequest.php?sid=1";
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
                        JSONObject r = data.getJSONObject(i);
                        request req = new request();

                        req.setScity(r.getString("s_city"));
                        req.setDcity(r.getString("d_city"));
                        req.setSaddr(r.getString("s_addr"));
                        req.setDaddr(r.getString("d_addr"));
//TODO                        req.setSHtype(r.getString("company_name"));
//                        req.setDHtype(r.getString("photo"));
                        req.setLoading(r.getString("request_loading"));
                        req.setSlift(r.getString("s_lift"));
                        req.setDlift(r.getString("d_lift"));
                        req.setDate(r.getString("date")+" "+r.getString("time"));

                        req.setItems(addImages());
                        mList.add(req);
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
            progressBar.setVisibility(View.GONE);
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
}

