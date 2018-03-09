package com.example.gptsi.roadie.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.shared_preferences;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class Change_Password extends AppCompatActivity {

    EditText old_pass,pass,confirm_pass;
    String semail,sold_pass,spass,sconfirm_pass,url;
    Button change_pass;
    Toolbar toolbar;
    shared_preferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_pass=(EditText)findViewById(R.id.opass);
        pass=(EditText)findViewById(R.id.pass);
        confirm_pass=(EditText)findViewById(R.id.confirmPass);
        change_pass=(Button)findViewById(R.id.btn_cngpass);
        toolbar=(Toolbar)findViewById(R.id.appbar);
        toolbar.setTitle("Change Password");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        sp = new shared_preferences(this);

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                semail = sp.getEmail();
                sold_pass=old_pass.getText().toString();
                spass=pass.getText().toString();
                sconfirm_pass=confirm_pass.getText().toString();

                boolean isEmpty = false;

                if (sold_pass.length() == 0 ){
                    old_pass.setError("");
                    old_pass.requestFocus();
                    isEmpty = true;
                } if (spass.length() == 0){
                    pass.setError("");
                    isEmpty = true;
                }if (sconfirm_pass.length() == 0){
                    confirm_pass.setError("");
                    isEmpty = true;
                } if(isEmpty) {
                    Toast.makeText(Change_Password.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } if(!spass.equals(sconfirm_pass)) {
                    Toast.makeText(Change_Password.this, "Passwords does not match", Toast.LENGTH_SHORT).show();
                    pass.setText("");
                    confirm_pass.setText("");
                    pass.setError("");
                    confirm_pass.setError("");
                } else {
                    url = IP_ADDRESS+"change_password.php?email=" + semail + "&old_pass=" + sold_pass + "&pass=" + spass + "&confirm_pass=" + sconfirm_pass;
                    new chg_pass().execute();
                }
            }
        });
    }

    class chg_pass extends AsyncTask<Void,Void,Void> {
        Json js;
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            js = new Json(Change_Password.this);
            result = js.processURL(url);
            Log.d("result",result);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(Change_Password.this, result, Toast.LENGTH_LONG).show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(Change_Password.this,Login.class));
                    Change_Password.this.finish();
                }
            });

            t.start();
        }
    }

}
