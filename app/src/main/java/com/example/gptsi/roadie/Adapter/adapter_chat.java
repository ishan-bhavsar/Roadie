package com.example.gptsi.roadie.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Pojo.chat;
import com.example.gptsi.roadie.R;
import com.stellaquila.ChatLayout;

import java.util.ArrayList;

/**
 * Created by GPTSI on 18-01-2018.
 */

public class adapter_chat extends BaseAdapter {

    Context mContext;
    ArrayList<chat> mList;
    LinearLayout imgLayout;
    ChatLayout mLayout;
    ImageView img;
    TextView caption;
    TextView msg;
    String TAG;
    ListView mParent = null;

    public adapter_chat(Context context, ArrayList<chat> list) {
        this.mContext = context;
        this.mList = list;
        TAG = mContext.getClass().getSimpleName();
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_chat,null);

        mLayout=view.findViewById(R.id.chat_layout);
        imgLayout=view.findViewById(R.id.chat_img_layout);
        img=view.findViewById(R.id.chat_img);
        caption=view.findViewById(R.id.chat_cap);
        msg=view.findViewById(R.id.chat_msg);

        chat c=mList.get(position);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//        Log.d(TAG,position+" "+c.getisSender());
//        if(c.getisSender()) {
        if(true){
            params.gravity = Gravity.RIGHT|Gravity.END;
            mLayout.setLayoutParams(params);
            mLayout.setSender(true);
        }else {
            params.gravity = Gravity.LEFT|Gravity.START;
            mLayout.setLayoutParams(params);
            mLayout.setSender(false);
        }

        switch (c.getMedia()){
            case chat.TEXT :
                msg.setText(c.getMsg());
                msg.setVisibility(View.VISIBLE);
                break;

            case chat.IMAGE :
                if(c.getImage().length()>0){
                    Glide.with(mContext).load(c.getImage()).into(img);
                    imgLayout.setVisibility(View.VISIBLE);
                    if(c.getCaption()!=null && c.getCaption().length()>0)
                        caption.setText(c.getCaption());
                    else
                        caption.setVisibility(View.GONE);
                }
                break;

            case chat.LOCATION:
                break;
        }
        mParent = ((ListView)parent);
        return view;
    }

    public void scrollBottom(){
        if(mParent!=null)
            mParent.setSelection(mList.size());
    }
}
