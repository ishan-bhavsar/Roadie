package com.example.gptsi.roadie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gptsi.roadie.Activity.Accepted_Bid;
import com.example.gptsi.roadie.Activity.Home;
import com.example.gptsi.roadie.Activity.ImageViewer;
import com.example.gptsi.roadie.Pojo.usr_history;
import com.example.gptsi.roadie.R;

import java.util.ArrayList;

/**
 * Created by GPTSI on 13-12-2017.
 */

public class adapter_history extends RecyclerView.Adapter<adapter_history.MyViewHolder>{

    Context mContext;
    ArrayList<usr_history> mList;
    String mDate=null;

    public adapter_history(Context mContext,ArrayList<usr_history> mList){
        this.mContext=mContext;
        this.mList=mList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txndate, price, src, dst, name;
        Button items;

        public MyViewHolder(View view) {
            super(view);

            txndate=(TextView)view.findViewById(R.id.h_txDate);
            price=(TextView)view.findViewById(R.id.h_price);
            src=(TextView)view.findViewById(R.id.h_src);
            dst=(TextView)view.findViewById(R.id.h_dst);
            name=(TextView)view.findViewById(R.id.h_name);
            items=(Button)view.findViewById(R.id.h_items);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_histroy, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        usr_history item = mList.get(position);

        holder.txndate.setVisibility(View.GONE);

        if(mDate==null || !mDate.equals(item.getTxDate())) {
            mDate = item.getTxDate();
            holder.txndate.setText(item.getTxDate());
            holder.txndate.setVisibility(View.VISIBLE);
        }

        holder.name.setText(item.getName());
        holder.txndate.setText(item.getTxDate());
        holder.price.setText(item.getPrice());
        holder.src.setText(item.getSrc());
        holder.dst.setText(item.getDst());

        holder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ImageViewer.class);
                i.putExtra("images",mList.get(position).getItems());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}