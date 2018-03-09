package com.example.gptsi.roadie.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.gptsi.roadie.Activity.Favourites;
import com.example.gptsi.roadie.Pojo.sp_base;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 09-12-2017.
 */

public class adapter_fav extends adapter_sp_base{

    Context mContext;
    Boolean mManage;
    ArrayList<Integer> mDelete;
    ViewGroup mParent;

    public adapter_fav(Context mContext, ArrayList<sp_base> mList) {
        super(mContext, mList);
        this.mContext = mContext;
        mDelete = new ArrayList<>();
        mManage = false;
    }

    public class MyViewHolder extends adapter_sp_base.MyViewHolder {

        CheckBox manage;

        public MyViewHolder(View view) {
            super(view);
            manage=(CheckBox)view.findViewById(R.id.mng_fav);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_sp_fav, parent, false);
        mParent=parent;
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final adapter_sp_base.MyViewHolder h, int position) {
        super.onBindViewHolder(h, position);
        final MyViewHolder holder = (MyViewHolder) h;
        if(mManage) {
            holder.manage.setVisibility(View.VISIBLE);
        }else{
            holder.manage.setVisibility(View.GONE);
        }

        holder.manage.setTag(new Integer(position));
        holder.manage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                    mDelete.add(mList.get((Integer)buttonView.getTag()).getId());
                else
                    mDelete.remove(mList.get((Integer)buttonView.getTag()).getId());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mManage = true;
                ConstraintLayout parent = (ConstraintLayout)mParent.getParent().getParent();
                BottomNavigationView navigation = (BottomNavigationView)parent.findViewById(R.id.bottom_navigation);
                navigation.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                //TODO frame Layout
                return false;
            }
        });
    }


    public void deleteFavourites()
    {
        new delete_favourites().execute();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class delete_favourites extends AsyncTask<Void, Void, Void> {
        Json js;
        String url = IP_ADDRESS+"deleteFavourites.php?uid=";
        String result;
        shared_preferences sp;

        public delete_favourites() {

            sp = new shared_preferences(mContext);
            url=url+sp.getId()+"&d="+(mDelete.toString().substring(1,mDelete.toString().length()-1).replaceAll(" ",""));
        }

        @Override
        protected Void doInBackground(Void...voids) {
            js = new Json(mContext);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("del ",""+result);
            notifyDataSetChanged();
            ((Activity)mContext).finish();
            mContext.startActivity(new Intent(mContext, Favourites.class));
            //TODO refresh page
        }
    }
}