package com.example.gptsi.roadie.Util.socialmediaIntegration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by GPTSI on 16-12-2017.
 */

/*
    //////////////// copy this in AndroidManifest.xml/////////////////////

    <meta-data android:name="com.facebook.sdk.ApplicationId"
        android:value="@string/facebook_app_id"/>

        <activity android:name="com.facebook.FacebookActivity"
            android:configChanges=
            "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
        android:label="@string/app_name" />
        <activity
            android:name="com.facebook.CustomTabActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="@string/fb_login_protocol_scheme" />
            </intent-filter>
        </activity>

    //////////////////////////////////////////////////////////////////////////

    ///////////////////// copy this in your activity.xml /////////////////////

    <com.facebook.login.widget.LoginButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:id="@+id/facebook_sign_in"/>
    ///////////////////////////////////////////////////////////////////////////

*/

public class facebookSignIn {

    private Context mContext;

    private LoginButton mLoginButton;
    private CallbackManager callbackManager;
    private static AccessTokenTracker accessTokenTracker;
    private static ProfileTracker profileTracker;
    static AccessToken accessToken = null;
    private static final String TAG = facebookSignIn.class.getSimpleName();


    public facebookSignIn(Context activity) {
        this.mContext = activity;

        FacebookSdk.sdkInitialize(mContext);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                accessToken = newToken;
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (newProfile != null) {
                    handleSignInResult(newProfile);
                }
            }
        };


        mLoginButton = (LoginButton) ((Activity) mContext).findViewById(R.id.facebook_sign_in);
        mLoginButton.registerCallback(callbackManager, callback);

        final Collection<String> permissions = Arrays.asList("email", "user_photos", "user_friends", "public_profile");

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions((Activity) mContext,permissions);
            }
        });

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

    }

    public static Boolean isLoggedIn(){
        return accessToken == null;
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            handleSignInResult(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Profile profile) {
        String personName = profile.getName();
        String personPhotoUrl = String.valueOf(profile.getProfilePictureUri(100, 100));
        String res = "Name: " + personName + ", Image: " + personPhotoUrl;
        Log.e(TAG, res);
        Toast.makeText(mContext, res, Toast.LENGTH_LONG).show();
    }

    public static void signOut() {
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        LoginManager.getInstance().logOut();
        Log.d(TAG,"logout");
    }
}
