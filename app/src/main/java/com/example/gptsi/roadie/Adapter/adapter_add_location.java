package com.example.gptsi.roadie.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.Activity.Accepted_Bid;
import com.example.gptsi.roadie.Activity.Chat;
import com.example.gptsi.roadie.Pojo.sp_base;
import com.example.gptsi.roadie.Pojo.sp_bid;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;
import static com.example.gptsi.roadie.Util.globals.SHARED_PREFERENCE_FILE;

/**
 * Created by GPTSI on 31-01-2018.
 */

public class adapter_add_location extends RecyclerView.Adapter<adapter_add_location.MyViewHolder>{

    Context mContext;
    ArrayList<String> mList;
    boolean mManage;

    public adapter_add_location(Context mContext,ArrayList<String> mList){
        this.mContext=mContext;
        this.mList=mList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView location;
        ImageView cancel;

        public MyViewHolder(View view) {
            super(view);

            location=(TextView)view.findViewById(R.id.add_location_name);
            cancel=(ImageView)view.findViewById(R.id.add_location_cancel);
            cancel.setColorFilter(Color.WHITE);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_add_location, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.location.setText(mList.get(position));

        if(mManage)
            holder.cancel.setVisibility(View.VISIBLE);
        else
            holder.cancel.setVisibility(View.GONE);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mList.size());
            }
        });
    }

    public void manage(boolean edit){
        this.mManage = edit;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}