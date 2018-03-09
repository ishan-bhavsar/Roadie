package com.example.gptsi.roadie.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Pojo.reviews;
import com.example.gptsi.roadie.Pojo.sp_base;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.web.downloadFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;
import static com.example.gptsi.roadie.Util.util.setListViewHeightBasedOnChildren;

/**
 * Created by GPTSI on 04-12-2017.
 */


public class adapter_sp_base extends RecyclerView.Adapter<adapter_sp_base.MyViewHolder> {

    Context mContext;
    Integer mId;
    ArrayList<sp_base> mOrignalList;
    ArrayList<sp_base> mList;
    ArrayList<reviews> mReviews;
    String TAG;

    public adapter_sp_base(Context mContext, ArrayList<sp_base> mList) {
        this.mContext = mContext;
        this.mOrignalList = mList;
        this.mList = new ArrayList<>();
        mReviews = new ArrayList<>();
        TAG = mContext.getClass().getSimpleName();
        Log.d(TAG, "conc Olist Len: " + mOrignalList.size());
        Log.d(TAG, "conc mList Len: " + this.mList.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, reviewCount, nullreviews;
        ImageView photo;
        RatingBar rating;
        Button reviewbtn;
        ListView reviews;
        LinearLayout progressbar;

        public MyViewHolder(View view) {
            super(view);

            photo = view.findViewById(R.id.sp_photo);
            name = view.findViewById(R.id.sp_name);
            rating = view.findViewById(R.id.sp_rating);
            reviewCount = view.findViewById(R.id.sp_reviewcount);
            reviewbtn = view.findViewById(R.id.sp_reviewbtn);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_sp_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        sp_base item = mList.get(position);
        mId = item.getId();
        downloadFile file = new downloadFile(mContext);
        file.download(item.getPhoto());
        Glide.with(mContext).load(item.getPhoto()).into(holder.photo);
        holder.name.setText(item.getName());
        holder.rating.setRating(item.getRating());
        holder.rating.setEnabled(false);
        holder.reviewCount.setText(item.getReviewCount());

        holder.reviewbtn.setTag(R.id.KEY_CLICKED, new Boolean(false));
        holder.reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_reviews);
                holder.nullreviews = dialog.findViewById(R.id.review_no_reviews);
                holder.reviews = dialog.findViewById(R.id.review_reviews);
                holder.progressbar = dialog.findViewById(R.id.progress_bar);
                ImageView cancel = dialog.findViewById(R.id.review_cancel);
                TextView name = dialog.findViewById(R.id.review_sp_name);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                mId = mList.get(position).getId();
                name.setText(mList.get(position).getName());
                dialog.show();
                new adapter_sp_base.get_reviews(holder).execute();
            }
        });
    }

    public void copyFromOriginal() {
        this.mList.addAll(mOrignalList);
        Log.d(TAG, "oncreate Olist Len: " + mOrignalList.size());
        Log.d(TAG, "oncreate mList Len: " + this.mList.size());
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.d(TAG, "filter: " + charText + " Len: " + charText.length());
        mList.clear();
        if (charText == null || charText.length() == 0) {
            mList.addAll(mOrignalList);
        } else {
            for (sp_base item : mOrignalList) {
                if (item.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(item);
                }
            }
        }
        Log.d(TAG, "filter: " + charText + " List Len: " + mList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class get_reviews extends AsyncTask<MyViewHolder, Void, Void> {

        Json js;
        String url = IP_ADDRESS + "getReviews.php?sid=" + mId;
        String result;
        MyViewHolder holder;

        public get_reviews(MyViewHolder holder) {
            this.holder = holder;
        }


        @Override
        protected Void doInBackground(MyViewHolder... myViewHolders) {

            js = new Json(mContext);
            result = js.processURL(url);

            try {

                if (result != null && result.length() > 3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject review = data.getJSONObject(i);
                        com.example.gptsi.roadie.Pojo.reviews r = new reviews();

                        r.setPhoto(review.getString("photo"));
                        r.setUser(review.getString("name"));
                        r.setRating((float) review.getDouble("rating"));
                        r.setReview(review.getString("review"));
                        mReviews.add(r);
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

            adapter_sp_review adp = new adapter_sp_review(mContext, mReviews);
            holder.reviews.setAdapter(adp);

            if (result.length() > 3) {
                holder.reviews.setVisibility(View.VISIBLE);
            } else {
                holder.nullreviews.setVisibility(View.VISIBLE);
            }
            holder.progressbar.setVisibility(View.GONE);
        }
    }
}