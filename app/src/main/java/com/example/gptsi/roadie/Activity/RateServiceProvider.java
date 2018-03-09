package com.example.gptsi.roadie.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.web.Json;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;
import static com.example.gptsi.roadie.Util.globals.SHARED_PREFERENCE_FILE;

public class RateServiceProvider extends AppCompatActivity {

    TextView mTitle;
    RatingBar mRating;
    EditText mReview;
    Button mSubmit;
    Toolbar toolbar;
    String rating,review,uid,sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_service_provider);

        mTitle=(TextView)findViewById(R.id.rate_sp_title);
        mRating=(RatingBar)findViewById(R.id.rate_sp_rating);
        mReview=(EditText)findViewById(R.id.rate_sp_review);
        mSubmit=(Button)findViewById(R.id.rate_sp_submit);
        toolbar=(Toolbar)findViewById(R.id.appbar);

        toolbar.setTitle("Rating");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        SharedPreferences mPref = getSharedPreferences(SHARED_PREFERENCE_FILE,MODE_PRIVATE);
        mTitle.setText("Rating for "+mPref.getString("service_provider","Service Provider"));
        uid=mPref.getString("uid","1");
        sid=mPref.getString("sid","1");

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=String.valueOf(mRating.getRating());
                review=mReview.getText().toString();
                new add_rating().execute();
            }
        });
    }

    class add_rating extends AsyncTask<Void, Void, Void> {

        Json js;
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            String url = IP_ADDRESS+"addRating.php?uid="+uid+"&sid"+sid+"&rating="+rating+"&review="+review;
            js = new Json(RateServiceProvider.this);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(RateServiceProvider.this, result, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RateServiceProvider.this,Home.class));
        }
    }

}
