package com.example.gptsi.roadie.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Pojo.User;
import com.example.gptsi.roadie.Pojo.chat;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.image_picker;
import com.example.gptsi.roadie.Util.util;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stellaquila.ChatLayout;

import java.io.File;
import java.util.ArrayList;

import static com.example.gptsi.roadie.Util.globals.SHARED_PREFERENCE_FILE;

/**
 * Created by Ishan Bhavsar on 28-02-2018.
 */

public class ChatFB extends AppCompatActivity {
    Toolbar toolbar;
    ListView messageList;
    EditText msgTxt;
    ImageView send;
    ImageView cam;
    image_picker picker;
    ArrayList<String> mPathList;
    String TAG = ChatFB.class.getSimpleName();
    DatabaseReference mDatabase;
    DatabaseReference sendMsg;
    String mUserId;
    StorageReference mStorage;
    final static String USERS = "UserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = findViewById(R.id.chat_list);
        msgTxt = findViewById(R.id.chat_msg_txt);
        send = findViewById(R.id.chat_send);
        cam = findViewById(R.id.chat_cam);
        toolbar = findViewById(R.id.chat_appbar);

        toolbar.setTitle("Chat");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        ////////////////////////////// Firebase ///////////////////////////////////////

        //Save Offline Data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences mPref = getSharedPreferences(SHARED_PREFERENCE_FILE,MODE_PRIVATE);

        if(!mPref.contains(USERS)) {
            mUserId = mDatabase.child("Users").push().getKey();
            SharedPreferences.Editor edit = mPref.edit();
            edit.putString(USERS,mUserId);
            edit.commit();

            User user = new User("Ishan", "i@gmail.com");
            mDatabase.child("Users").child(mUserId).setValue(user);

        }else {
            mUserId = mPref.getString(USERS,"");
        }

        mDatabase = mDatabase.child("Messages");

        mStorage = FirebaseStorage.getInstance().getReference();

        ///////////////////////////////////////////////////////////////////////////////

        msgTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    cam.setVisibility(View.VISIBLE);
//                    EditText TODO change layout weight
                } else {
                    cam.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        picker = new image_picker(ChatFB.this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(msgTxt.getText().toString().trim())) {
                    chat c = new chat();
                    c.setSender(mUserId);
                    c.setMsg(msgTxt.getText().toString());
                    sendMsg = mDatabase.push();
                    sendMsg.setValue(c,sendMsg.getKey());
                    msgTxt.setText("");
                    util.hideKeyboard(ChatFB.this);
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
        picker.onActivityResult(requestCode, resultCode, data);
        mPathList = picker.getPathList();

        for (String path : mPathList) {

            final Uri image = Uri.fromFile(new File(path));
            mStorage.child(mUserId).putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            chat c = new chat();
                            c.setSender(mUserId);
                            c.setImage("gs://roadie-2bc9d.appspot.com/"+taskSnapshot.getDownloadUrl().getPath());
                            c.setMedia(chat.IMAGE);
                            sendMsg = mDatabase.push();
                            sendMsg.setValue(c,sendMsg.getKey());
                            Log.d(TAG, "upload Success for: " + image.getPath());
                            Log.d(TAG, "upload Success for: " + taskSnapshot.getDownloadUrl().getHost());
                            Log.d(TAG, "upload Success for: " + taskSnapshot.getDownloadUrl().getPath());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "upload Failed for: " + image.getPath() + "\n" + e.getMessage());
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

                Query query = FirebaseDatabase.getInstance()
//                .getReference("notfication/unread/" + userMailAddress.replace(".", ","))
                .getReference().child("Messages")
                .orderByKey();
//                Query query = mDatabase.child(key).child("msg").orderByValue();

        FirebaseListOptions<chat> options = new FirebaseListOptions.Builder<chat>()
                .setLayout(R.layout.single_chat)
                .setQuery(query, chat.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseListAdapter<chat> adapter = new FirebaseListAdapter<chat>(options) {

            @Override
            protected void populateView(View view, chat model, int position) {
                ChatLayout mLayout = view.findViewById(R.id.chat_layout);
                LinearLayout imgLayout = view.findViewById(R.id.chat_img_layout);
                ImageView img = view.findViewById(R.id.chat_img);
                TextView caption = view.findViewById(R.id.chat_cap);
                TextView msg = view.findViewById(R.id.chat_msg);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                Log.d(TAG, "Populate View: pos: " + position + " Message: " + model.getMsg());

                if (model.getSender().equals(mUserId)) {
                    params.gravity = Gravity.RIGHT | Gravity.END;
                    mLayout.setLayoutParams(params);
                    mLayout.setSender(true);
                } else {
                    params.gravity = Gravity.LEFT | Gravity.START;
                    mLayout.setLayoutParams(params);
                    mLayout.setSender(false);
                }

                switch (model.getMedia()) {
                    case chat.TEXT:
                        msg.setText(model.getMsg());
                        msg.setVisibility(View.VISIBLE);
                        break;

                    case chat.IMAGE:
                        if (!TextUtils.isEmpty(model.getImage())) {
                            //TODO Download Image
                            Glide.with(ChatFB.this).load(model.getImage()).into(img);
                            imgLayout.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(model.getCaption()))
                                caption.setText(model.getCaption());
                            else
                                caption.setVisibility(View.GONE);
                        }
                        break;

                    case chat.LOCATION:
                        break;
                }
            }
        };

        messageList.setAdapter(adapter);
    }
}




