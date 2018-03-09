package com.example.gptsi.roadie.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gptsi.roadie.Adapter.adapter_chat;
import com.example.gptsi.roadie.Pojo.chat;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.SQLite;
import com.example.gptsi.roadie.Util.firebase.firebase;
import com.example.gptsi.roadie.Util.firebase.util.message;
import com.example.gptsi.roadie.Util.firebase.util.messageReceivedCallback;
import com.example.gptsi.roadie.Util.image_picker;
import com.example.gptsi.roadie.Util.util;
import com.example.gptsi.roadie.Util.web.Json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class Chat extends AppCompatActivity {

    firebase firebaseMsg;
    messageReceivedCallback callback;
    Toolbar toolbar;
    ListView listView;
    EditText msgTxt;
    ImageView send;
    ImageView cam;
    adapter_chat adapter;
    ArrayList<chat> mList;
    image_picker picker;
    ArrayList<String> mPathList;
    SQLite sqLite;
    String TAG = Chat.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView=findViewById(R.id.chat_list);
        msgTxt =findViewById(R.id.chat_msg_txt);
        send=findViewById(R.id.chat_send);
        cam=findViewById(R.id.chat_cam);
        toolbar=findViewById(R.id.chat_appbar);

        toolbar.setTitle("Chat");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        sqLite = new SQLite(Chat.this,"DB",1,"reg",chat.class);
        ArrayList<Object> list = sqLite.getdata();

        mList = new ArrayList<>();

        if(list!= null)
        for (int i = 0;i < list.size();i++){
            mList.add((chat)list.get(i));
        }

        adapter = new adapter_chat(Chat.this,mList);
        listView.setAdapter(adapter);
        adapter.scrollBottom();

        callback = new messageReceivedCallback() {
            @Override
            public void onMessageReceived(message msg) {

                chat c = new chat();

                if(msg.getImageUrl().length()>0){
//                    c.setImage(msg.getImageUrl());
                    c.setCaption(msg.getMessage());
                    c.setMedia(chat.IMAGE);
                }
                if(msg.getMessage().length()>0) {
                    c.setMsg(msg.getMessage());
                }
                mList.add(c);
                sqLite.insert(c);
                adapter.notifyDataSetChanged();
                adapter.scrollBottom();
            }
        };

        firebaseMsg = new firebase(Chat.this,callback);

        msgTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0){
                    cam.setVisibility(View.VISIBLE);
//                    EditText TODO change layout weight
                }else {
                    cam.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        picker = new image_picker(Chat.this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgTxt.getText().toString().length()>0) {
                    chat c = new chat();
//                    c.setisSender();
                    c.setMsg(msgTxt.getText().toString());
                    mList.add(c);
                    sqLite.insert(c);
                    message msg = new message();
                    msg.setMessage(msgTxt.getText().toString());
                    msg.setRegId("dPx0WDyvwm4:APA91bHsIWr-cczAFQZFYDJjTRbvLyVqiBiu6W4xkpa5-KgYzSLQ371oeIOOlIllYUETq-4BDf-B3_SBXS86TpgDuAsm16oJgTnol-q4-7lwve2WzBcjYHxjOpTLIlSTJEGBfS9i_zlr");
                    firebaseMsg.sendMessage(msg);
//                    new send_msg().execute(c);
                    adapter.notifyDataSetChanged();
                    adapter.scrollBottom();
                    msgTxt.setText("");
                    util.hideKeyboard(Chat.this);
                }
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              picker.openGallery(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.onActivityResult(requestCode,resultCode,data);
        mPathList = picker.getPathList();

        for(String path:mPathList) {
            chat c = new chat();
//            c.setisSender();
//            c.setImage(path);
            c.setMedia(chat.IMAGE);
            mList.add(c);
            sqLite.insert(c);
//            new send_msg().execute(c);
            message msg = new message();
            msg.setImageUrl(path);
            msg.setRegId("es7ZgEi4op4:APA91bEOuqmMZML0y6zpNni-yxVGcJcTioRR5SEHA8FEtR9t7CRt2biDik0OhMJkmt7Y3sVj06Qixh5Hccx8q_UeAyDr0nQ-qNMaTupMOKugy5Z7yM91mJ06RMFuICRyhUYhhGgqEjiI");
            firebaseMsg.sendMessage(msg);
        }
        adapter.notifyDataSetChanged();
        adapter.scrollBottom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseMsg.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseMsg.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        msgTxt.clearFocus();
        util.hideKeyboard(Chat.this);
    }


    class send_msg extends AsyncTask<chat, Void, Void> {

        Json js;
        String result;
        String regId = 1==1?"fhwJP9utwXE:APA91bGLaHVz0LHgNJGFZqpVUkJ8rb6XDsJw4V_9T5JVNo7jx4czC1lS5Os-skifU8uWyzd4YNZzVtCMl9MPEf2d0ZbOzcA-jDf5dAJTxx1LaP899DqsYrAi7ziZdo4APsHGl6Tmr35x":"dPx0WDyvwm4:APA91bHsIWr-cczAFQZFYDJjTRbvLyVqiBiu6W4xkpa5-KgYzSLQ371oeIOOlIllYUETq-4BDf-B3_SBXS86TpgDuAsm16oJgTnol-q4-7lwve2WzBcjYHxjOpTLIlSTJEGBfS9i_zlr";

        @Override
        protected Void doInBackground(chat... chats) {
            String url = IP_ADDRESS+"firebase/index.php?regId="+regId+"&title=&message="+chats[0].getMsg()+"&image="+chats[0].getImage()+"&push_type=individual";
            js = new Json(Chat.this);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(Chat.this, result, Toast.LENGTH_SHORT).show();
        }
    }

}
