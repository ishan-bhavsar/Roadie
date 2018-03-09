package com.example.gptsi.roadie.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.address_autofill;
import com.example.gptsi.roadie.Util.image_picker;
import com.example.gptsi.roadie.Util.util;
import com.example.gptsi.roadie.Util.web.Json;
import com.example.gptsi.roadie.Util.web.uploadFile;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;


public class Register extends AppCompatActivity {

    String TAG = Register.class.getSimpleName();
    Toolbar toolbar;
    EditText fname, lname, addr, phone, email, pass, confirmPass;
    AutoCompleteTextView city,state;

    String sfname,slname,saddr,scity,sstate,snum,semail,spass,sconfirmPass,url,result;
    Button btnreg,btnTkphoto,btnLib,btnCnc;
    ImageView btnphoto;
    Dialog img_dialog;
    image_picker picker;
    Boolean isCamera = false;
    Boolean isValidEmail = false;
    Boolean isValidPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        addr = findViewById(R.id.addr);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.confirmPass);
        btnreg = findViewById(R.id.btnreg);
        btnphoto = findViewById(R.id.btnphoto);
        toolbar = findViewById(R.id.appbar);

        toolbar.setTitle("Register");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        final address_autofill autofill = new address_autofill(Register.this);
        state.setAdapter(autofill.getStateAdapter());
        state.setThreshold(0);

        state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                city.setAdapter(autofill.getCityAdapter(state.getText().toString()));
                city.setThreshold(0);
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s;
                s=email.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    email.setError("Invalid Email");
                }else
                    isValidEmail = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!pass.getText().toString().equals(confirmPass.getText().toString())) {
                    confirmPass.setError("Password does not match");
                }else
                    isValidPassword = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        picker = new image_picker(Register.this);

        btnphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_dialog = new Dialog(Register.this);
                img_dialog.setContentView(R.layout.dialog_photo);
                btnTkphoto=(Button)img_dialog.findViewById(R.id.btn_tkphoto);
                btnLib=(Button)img_dialog.findViewById(R.id.btn_lib);
                btnCnc=(Button)img_dialog.findViewById(R.id.btn_cnc);
                img_dialog.setTitle("Add Photo");
                img_dialog.show();

                btnTkphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        picker.openCamera();
                        img_dialog.dismiss();
                        isCamera=true;
                    }
                });

                btnLib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        picker.openGallery(false);
                        img_dialog.dismiss();
                        isCamera=false;
                    }
                });

                btnCnc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        img_dialog.dismiss();
                    }
                });
            }
        });

        btnreg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {

                sfname = fname.getText().toString();
                slname = lname.getText().toString();
                saddr = addr.getText().toString();
                scity = city.getText().toString().toLowerCase();
                sstate = state.getText().toString().toLowerCase();
                snum = phone.getText().toString();
                semail = email.getText().toString();
                spass = pass.getText().toString();
                sconfirmPass = confirmPass.getText().toString();

                boolean isEmpty = false;

                if (sfname.length() == 0 ){
                    util.showError(fname);
                    isEmpty = true;
                } if ( slname.length() == 0){
                    util.showError(lname);
                    isEmpty = true;
                } if ( saddr.length() == 0){
                    util.showError(addr);
                    isEmpty = true;
                } if ( scity.length() == 0){
                    util.showError(city);
                    isEmpty = true;
                } if ( sstate.length() == 0){
                    util.showError(state);
                    isEmpty = true;
                } if ( snum.length() == 0){
                    util.showError(phone);
                    isEmpty = true;
                } if ( !isValidEmail){
                    util.showError(email);
                    isEmpty = true;
                } if ( spass.length() == 0){
                    util.showError(pass);
                    isEmpty = true;
                } if ( !isValidPassword){
                    util.showError(confirmPass);
                    isEmpty = true;
                }if( picker.getPath()==null){
                    util.showError(btnphoto);
                    isEmpty = true;
                }
                if (isEmpty) {
                    Toast.makeText(Register.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                } else {

                    new Thread(new Runnable() {
                        public void run() {

                            Log.d(TAG,"uploading started.....");
                            uploadFile u = new uploadFile();
                            u.upload(picker.getPath(),isCamera);

                        }
                    }).start();

                    url = IP_ADDRESS+"register.php?name="+sfname+" "+slname+"&addr="+saddr+"&city="+scity+"&state="+sstate+"&phone="+snum+"&email="+semail+"&pass="+spass+"&photo="+picker.getPath();
                    new reg().execute();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picker.onActivityResult(requestCode,resultCode,data);
        btnphoto.setImageBitmap(picker.getImage());
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length == 0) {
            picker.askPermission();
        }
    }

    class reg extends AsyncTask<Void,Void,Void>{
        Json js;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Register.this);
            pd.setTitle("Registering...");
            pd.setMessage("Please Wait");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            js = new Json(Register.this);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(pd.isShowing()) {
                pd.dismiss();
            }

            Toast.makeText(Register.this, result, Toast.LENGTH_LONG).show();

            if(result.equals(Json.SUCCESS)) {

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }
                });

                t.start();
            }
        }
    }
}
