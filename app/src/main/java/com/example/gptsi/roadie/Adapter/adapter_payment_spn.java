package com.example.gptsi.roadie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Pojo.payment;
import com.example.gptsi.roadie.R;

import java.util.ArrayList;

/**
 * Created by GPTSI on 31-01-2018.
 */

public class adapter_payment_spn extends BaseAdapter {

    Context mContext;
    ArrayList<payment> mList;

    public adapter_payment_spn(Context mContext, ArrayList<payment> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.single_spinner_payment,null);

        ImageView image=(ImageView)view.findViewById(R.id.payment_img);
        TextView name=(TextView)view.findViewById(R.id.payment_name);

        payment item = mList.get(position);
        image.setImageResource(item.getImage());
//        Glide.with(mContext).load().into(image);
        name.setText(item.getName());

        return view;
    }
}
