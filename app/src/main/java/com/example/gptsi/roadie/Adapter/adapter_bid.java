package com.example.gptsi.roadie.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
 * Created by GPTSI on 12-12-2017.
 */

public class adapter_bid extends adapter_sp_base{

    Context mContext;
    ArrayList<sp_bid> mList;
    SharedPreferences mPref;
    String sname,sid,bid;
    static final int REQUEST_PHONE=202;

    public adapter_bid(Context mContext,ArrayList<? extends sp_base> mList) {
        super(mContext, (ArrayList<sp_base>) mList);
        this.mContext = mContext;
        this.mList = (ArrayList<sp_bid>) mList;
    }

    public class MyViewHolder extends adapter_sp_base.MyViewHolder {

        TextView price;
        Button accept;
        ImageButton phone,chat;

        public MyViewHolder(View view) {
            super(view);

            price=view.findViewById(R.id.bid_price);
            accept=view.findViewById(R.id.bid_accept);
            phone=view.findViewById(R.id.bid_phone);
            chat=view.findViewById(R.id.bid_chat);
            mPref = mContext.getSharedPreferences(SHARED_PREFERENCE_FILE,Context.MODE_PRIVATE);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_sp_bid, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final adapter_sp_base.MyViewHolder h, final int position) {
        super.onBindViewHolder(h, position);
        final MyViewHolder holder = (MyViewHolder) h;

        holder.price.setText("Price: "+mList.get(position).getPrice());

        holder.accept.setTag(new Integer(position));
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sid = mList.get((Integer) v.getTag()).getId().toString();
                sname = mList.get((Integer) v.getTag()).getName().toString();
                bid = mList.get((Integer) v.getTag()).getPrice().toString();
                SharedPreferences.Editor editor = mPref.edit();
                editor.putString("sid",sid);
                editor.putString("service_provider",sname);
                editor.commit();
//                new accept_bid().execute(); TODO comment next line
                mContext.startActivity(new Intent(mContext,Accepted_Bid.class));
            }
        });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + mList.get(position).getPhone()));

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    mContext.startActivity(i);
                }else {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE);
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        mContext.startActivity(i);
                    }
                }
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Chat.class);
                //TODO i.putExtra("chat_Id",mList.get(position).getChatId());
                mContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class accept_bid extends AsyncTask<Void,Void,Void> {
        Json js;
        String result, url = IP_ADDRESS + "acceptBid.php?uid=" + mPref.getString("uid", "1")+"&sid="+sid+"&bid="+bid+"&bid_accepted=1";

        @Override
        protected Void doInBackground(Void... voids) {
            js = new Json(mContext);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mContext.startActivity(new Intent(mContext, Accepted_Bid.class));
                    ((Activity) mContext).finish();
                }
            });
            t.start();
        }
    }
}