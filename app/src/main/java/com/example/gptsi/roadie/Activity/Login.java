package com.example.gptsi.roadie.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.socialmediaIntegration.facebookSignIn;
import com.example.gptsi.roadie.Util.socialmediaIntegration.googleSignIn;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;
import com.example.gptsi.roadie.Util.web.downloadFile;
import com.facebook.Profile;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class Login extends AppCompatActivity {

    AutoCompleteTextView email;
    EditText pass;
    Button login;
    String url,result;
    String suid,sname,semail,saddr,scity,sstate,snum,spass,photo;
    TextView forget_pass, register;
    Switch remId;
    LinearLayout layout;
    int mRemId =0;
    shared_preferences sp;
    downloadFile file;

    private googleSignIn googlePlus;
    private facebookSignIn facebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=findViewById(R.id.btnlogin);
        register=findViewById(R.id.btnreg);
        forget_pass=findViewById(R.id.forget_pass);
        email=findViewById(R.id.edtemail);
        pass=findViewById(R.id.edtpass);
        remId=findViewById(R.id.sw_remId);
        layout=findViewById(R.id.layout);

//        TODO hide keyboard ??????????????????????????????????????
//        layout.setFocusable(true);
//        layout.setFocusableInTouchMode(true);

        sp = new shared_preferences(this);
        if(sp.isRegUser()) {
            startActivity(new Intent(Login.this, Home.class));
            Login.this.finish();
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(Login.this,android.R.layout.simple_spinner_dropdown_item,sp.getRegUsers());
        email.setAdapter(adp);
        email.setThreshold(0);

        mRemId = sp.getRemId();
        if(mRemId ==1) {
             remId.setChecked(true);
         }

         if(remId.isChecked()){
            email.setText(sp.getEmail());
            pass.requestFocus();
        }

        remId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mRemId = (b) ? 1 : 0;
            }
        });

        forget_pass.setPaintFlags(forget_pass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forget_pass.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(Login.this,Change_Password.class);
                 startActivity(i);
             }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                semail=email.getText().toString();
                spass=pass.getText().toString();
                boolean isEmpty = false;

                if (semail.length() == 0 ){
                    email.setError("");
                    email.requestFocus();
                    isEmpty = true;
                } if ( spass.length() == 0){
                    pass.setError("");
                    isEmpty = true;
                } if(isEmpty) {
                    Toast.makeText(Login.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    url = IP_ADDRESS+"login.php?email=" + semail + "&pass=" + spass;
                    new select().execute();
                }

            }
        });

        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });

        ///////////////google sign In//////////////////////////
        googlePlus = new googleSignIn(Login.this);

        ////////////// facebook sign in /////////////////////
        facebook = new facebookSignIn(Login.this);

        file = new downloadFile(Login.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.onActivityResult(requestCode, resultCode, data);
        googlePlus.onActivityResult(requestCode, resultCode, data);
    }

    class select extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog pd;
        Json js;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
            pd.setTitle("Sigining In...");
            pd.setMessage("Please Wait");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            js=new Json(Login.this);
            result=js.processURL(url);

            try {
                if(result!=null && result.length()>3) {

                    JSONObject res = new JSONObject(result);
                    JSONArray data = res.getJSONArray("data");
                    JSONObject contact = data.getJSONObject(0);

                    suid = contact.getString("uid");
                    sname = contact.getString("name");
                    semail = contact.getString("email");
                    saddr = contact.getString("addr");
                    scity = contact.getString("city");
                    sstate = contact.getString("state");
                    snum = contact.getString("phone");
                    spass = contact.getString("pass");
                    photo=contact.getString("photo");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(pd.isShowing()) {
                pd.dismiss();
            }

            if(result.length()<3)
            {
                Toast.makeText(Login.this, "invalid username or password", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(Login.this, "login success", Toast.LENGTH_SHORT).show();

                photo = file.download(photo);

                sp.setPhoto(photo);
                sp.setId(suid);
                sp.setName(sname);
                sp.setEmail(semail);
                sp.setAddr(saddr);
                sp.setCity(scity);
                sp.setState(sstate);
                sp.setNum(snum);
                sp.setPass(spass);
                sp.setRemId(mRemId);
                sp.addUser(semail);
                sp.setUserType(shared_preferences.SERVICEPROVIDER);

                startActivity(new Intent(Login.this,Home.class));
                Login.this.finish();

            }
        }
    }
}
