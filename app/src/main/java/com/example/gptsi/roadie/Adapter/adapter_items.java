package com.example.gptsi.roadie.Adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.R;

import java.util.ArrayList;

/**
 * Created by GPTSI on 15-12-2017.
 */

public class adapter_items extends RecyclerView.Adapter<adapter_items.MyViewHolder>{

    Context mContext;
    ArrayList<String> mList;
    Boolean mManage = false;
    ArrayList<String> mDelete;
    String TAG;
    ViewGroup mParent;

    public adapter_items(Context mContext,ArrayList<String> list) {
        this.mContext = mContext;
        this.mList=list;
        TAG = mContext.getClass().getSimpleName();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView photo;
        CheckBox manage;

        public MyViewHolder(View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.items_photo);
            manage = itemView.findViewById(R.id.items_chk);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_item, parent, false);

        mParent = parent;

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if(mManage) {
            holder.manage.setVisibility(View.VISIBLE);
        }else {
            Glide.with(mContext).load(mList.get(position)).into(holder.photo);
            holder.manage.setVisibility(View.GONE);
        }

        holder.manage.setChecked(false);
        holder.manage.setTag(new Integer(position));
        holder.manage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(position>mList.size()-1)
                    return;

                String h = mList.get(position);
                if (isChecked) {
                    mDelete.add(h);
                } else {
                    mDelete.remove(h);
                }
            }
        });

        holder.photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mManage = true;
                mDelete = new ArrayList<>();
                ConstraintLayout parent = (ConstraintLayout)mParent.getParent().getParent().getParent();
                ImageButton delete = parent.findViewById(R.id.items_delete);
                delete.setVisibility(View.VISIBLE);
                parent.findViewById(R.id.items_cam).setEnabled(false);
                parent.findViewById(R.id.items_gal).setEnabled(false);
                notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ArrayList<String> deleteImages(){
        ArrayList<String> list = new ArrayList<>();
        list.addAll(mDelete);
        mDelete.clear();
        mManage =false;
        return list;
    }
}