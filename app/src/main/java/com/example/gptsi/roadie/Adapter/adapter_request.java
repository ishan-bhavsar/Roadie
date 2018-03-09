package com.example.gptsi.roadie.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.Activity.ImageViewer;
import com.example.gptsi.roadie.Pojo.request;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.shared_preferences;
import com.example.gptsi.roadie.Util.util;
import com.example.gptsi.roadie.Util.web.Json;

import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 04-12-2017.
 */


public class adapter_request extends RecyclerView.Adapter<adapter_request.MyViewHolder>{

    Context mContext;
    ArrayList<request> mList;
    int mPosition;

    public adapter_request(Context mContext, ArrayList<request> mList){
        this.mContext=mContext;
        this.mList=mList;
        this.mPosition = 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView scity, dcity, date;
        Button items;
        ImageButton send;
        EditText bid;

        public MyViewHolder(View view) {
            super(view);

            scity = view.findViewById(R.id.request_scity);
            dcity = view.findViewById(R.id.request_dcity);
            date = view.findViewById(R.id.request_date);
            items = view.findViewById(R.id.request_items);
            send = view.findViewById(R.id.request_send);
            bid = view.findViewById(R.id.request_bid);
        }

    }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_request, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final request item = mList.get(position);
//            downloadFile file = new downloadFile();
//            file.download(item.getPhoto());
//            Glide.with(mContext).load(item.getPhoto()).into(holder.photo);
            holder.scity.setText(item.getScity());
            holder.dcity.setText(item.getDcity());
            holder.date.setText(item.getDate());

            holder.scity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_request_address);

                    TextView addr,htype,bedroom,loading;
                    LinearLayout layout;

                    addr = dialog.findViewById(R.id.dialog_addr);
                    htype = dialog.findViewById(R.id.dialog_htyp);
                    bedroom = dialog.findViewById(R.id.dialog_bedrooms);
                    loading = dialog.findViewById(R.id.dialog_loading);
                    layout = dialog.findViewById(R.id.dialog_layout);

                    addr.setText(item.getSaddr());
                    htype.setText(item.getSHtype());
                    bedroom.setText(item.getSbedroom());
                    loading.setText(item.getLoading());
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            holder.dcity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_request_address);

                    TextView addr,htype,bedroom,loading;
                    LinearLayout layout;

                    addr = dialog.findViewById(R.id.dialog_addr);
                    htype = dialog.findViewById(R.id.dialog_htyp);
                    bedroom = dialog.findViewById(R.id.dialog_bedrooms);
                    loading = dialog.findViewById(R.id.dialog_loading);
                    layout = dialog.findViewById(R.id.dialog_layout);

                    addr.setText(item.getDaddr());
                    htype.setText(item.getDHtype());
                    bedroom.setText(item.getDbedroom());
                    loading.setText(item.getLoading());
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            holder.items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ImageViewer",mList.get(position).getItems().toString());
                    Intent i = new Intent(mContext, ImageViewer.class);
                    i.putExtra("images",mList.get(position).getItems());
                    mContext.startActivity(i);
                }
            });

            holder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPosition = position;
                    if(holder.bid.getText().toString().length()>0)
                        new send_bid().execute(holder.bid.getText().toString());
                }
            });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class send_bid extends AsyncTask<String, Void, Void> {

        Json js;
        shared_preferences sp;
        String url = IP_ADDRESS+"sendBid.php"; //TODO add method to differentiate betn user and sp MAKE PHP FILE
        String result;

        public send_bid() {
            sp = new shared_preferences(mContext);
            url = url + "?sid="+sp.getId();
        }

        @Override
        protected Void doInBackground(String... strings) {

            js = new Json(mContext);
            url = url+"&bid="+strings[0];
            Log.d("BID",url);
            result = js.processURL(url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
            mList.remove(mPosition);
            notifyItemRemoved(mPosition);
            notifyItemRangeChanged(mPosition,mList.size());
            util.hideKeyboard(mContext);
//            notifyDataSetChanged();
        }
    }
}