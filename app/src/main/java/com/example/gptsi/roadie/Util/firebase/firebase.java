package com.example.gptsi.roadie.Util.firebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.gptsi.roadie.Util.firebase.app.Config;
import com.example.gptsi.roadie.Util.firebase.util.NotificationUtils;
import com.example.gptsi.roadie.Util.firebase.util.message;
import com.example.gptsi.roadie.Util.firebase.util.messageReceivedCallback;
import com.example.gptsi.roadie.Util.web.Json;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

/**
 * Created by GPTSI on 16-01-2018.
 */

public class firebase {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Context mContext;
    private messageReceivedCallback mCallback;
    private final static String TAG = firebase.class.getSimpleName();

    class messageHelper {
        boolean isDelivered;
        boolean isRead;
        message mMsg;

        public messageHelper(message mMsg) {
            this.isDelivered = false;
            this.isRead = false;
            this.mMsg = mMsg;
        }
    }

    private HashMap<String,messageHelper> sentMessages;


    public firebase(Context context, messageReceivedCallback callback) {

        this.mContext = context;
        this.mCallback = callback;

        sentMessages = new HashMap<String,messageHelper>(){

            @Override
            public boolean remove(Object key, Object value) {
                Log.d(TAG,""+key);
                return super.remove(key, value);
            }
        };

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    message msg = (message) intent.getSerializableExtra("message");
                    mCallback.onMessageReceived(msg);

                    //TODO
                    for (String key : sentMessages.keySet()) {
                        messageHelper value = sentMessages.get(key);
                        if(value.isRead) {
                            sentMessages.remove(key);
                        }
                    }

                    Toast.makeText(mContext, "Push notification: " + msg.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();
    }

    public void sendMessage(message msg){
        new send_msg(msg).execute();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = mContext.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("firebase", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.d("firebase1","Firebase Reg Id: " + regId);
        else
            Log.d("firebase1", "Firebase Reg Id is not received yet!");
    }

    public void onResume() {

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(mContext);
    }

    public void onPause() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    class send_msg extends AsyncTask<Void, Void, Void> {
        message mMsg;
        Json js;
        String result;
        String url;

        public send_msg(message msg){
            this.mMsg = msg;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            url = IP_ADDRESS+"firebase/index.php?regId="+mMsg.getRegId()+"&message_id="+mMsg.getMessageId()+
                    "&message="+mMsg.getMessage()+"&image="+mMsg.getImageUrl()+"&push_type="+mMsg.getPushType();
            js = new Json(mContext);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            messageHelper msg = new messageHelper(mMsg);
            sentMessages.put(mMsg.getMessageId(),msg);
            Log.d(TAG,"url "+url);
            Log.d(TAG,"Message sent: "+mMsg.getMessage()+"Result: "+result);
        }
    }
}
