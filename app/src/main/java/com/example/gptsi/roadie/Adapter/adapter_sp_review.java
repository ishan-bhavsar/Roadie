package com.example.gptsi.roadie.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Pojo.reviews;
import com.example.gptsi.roadie.R;

import java.util.ArrayList;

/**
 * Created by GPTSI on 04-12-2017.
 */

public class adapter_sp_review extends BaseAdapter {
    Context mContext;
    ArrayList<reviews> mList;

    public adapter_sp_review(Context mContext, ArrayList<reviews> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.single_sp_reviews,null);

        ImageView photo=(ImageView) view.findViewById(R.id.usr_photo);
        TextView name=(TextView)view.findViewById(R.id.usr_name);
        RatingBar rating=(RatingBar)view.findViewById(R.id.usr_rating);
        TextView review=(TextView)view.findViewById(R.id.usr_review);

        reviews item = mList.get(position);
        Glide.with(mContext).load(item.getPhoto()).into(photo);
        name.setText(item.getUser());
        rating.setRating(item.getRating());
        rating.setEnabled(false);
        review.setText(item.getReview());

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

}
