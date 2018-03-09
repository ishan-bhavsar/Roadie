package com.example.gptsi.roadie.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.shared_preferences;
import com.example.gptsi.roadie.Util.web.Json;

import static com.example.gptsi.roadie.Util.globals.IP_ADDRESS;

public class Add_Request_slider extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private View[] views;
    private Object[] pages;
    private Button prev, next;
    LinearLayout welcome,bottom_view;
    ImageView welcome_img;
    TextView welcome_txt;
    Toolbar toolbar;
    SharedPreferences mPref;
    shared_preferences sp;
    String From = "Source",To="Destination";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request_slider);

        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        prev = findViewById(R.id.btn_prev);
        next = findViewById(R.id.btn_next);
        welcome = findViewById(R.id.welcome);
        welcome_img = findViewById(R.id.welcome_img);
        welcome_txt = findViewById(R.id.welcome_txt);
        bottom_view = findViewById(R.id.bottom_view);
        toolbar = findViewById(R.id.appbar);

        toolbar.setTitle("Add Request");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        sp = new shared_preferences(Add_Request_slider.this);
        mPref = getSharedPreferences("AddRequest", MODE_PRIVATE);

        welcome_img.setColorFilter(getResources().getColor(R.color.colorPrimary));

        if(mPref.contains("edit"))
            welcome_txt.setText("Edit");
        else
            welcome_txt.setText("Create");

        bottom_view.setVisibility(View.GONE);

        welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcome.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                bottom_view.setVisibility(View.VISIBLE);
            }
        });

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.activity_address,
                R.layout.activity_address,
                R.layout.activity_time,
                R.layout.activity_items};

        views = new View[layouts.length];

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int position = 0; position < layouts.length; position++) {
            View view = layoutInflater.inflate(layouts[position], null, false);
            views[position] = view;
        }


        pages = new Object[layouts.length];
        pages[0] = new Address(Add_Request_slider.this, views[0], true);
        pages[1] = new Address(Add_Request_slider.this, views[1], false);
        pages[2] = new Time(Add_Request_slider.this, views[2]);
        pages[3] = new Items(Add_Request_slider.this, views[3]);


        // adding bottom dots
        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        prev.setVisibility(View.INVISIBLE);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = currentPage();
                if (current > 0) {
                    // move to prev screen
                    viewPager.setCurrentItem(current-1);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean submit = false;
                int current = currentPage();
                if (current < layouts.length) {

                    switch (current) {
                        case 0:
                        case 1:
                            submit = ((Address) pages[current]).submit();
                            break;

                        case 2:
                            submit = ((Time) pages[current]).submit();
                            break;

                        case 3:
                            ((Items) pages[current]).submit();
                            submit();
                            submit = true;
                            break;
                    }

                    // move to next screen
                    if (submit) {
                        if(current==layouts.length)
                            return;
                        else {
                            viewPager.setCurrentItem(current + 1);
                            SharedPreferences.Editor edit = mPref.edit();
                            edit.putBoolean("edit",true);
                            edit.commit();
                        }
                    }
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(25);
            dots[i].setGravity(Gravity.CENTER);
            dots[i].setTextColor(getResources().getColor(R.color.lightGrey));   // Inactive Color
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextSize(35);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 10;
            dots[currentPage].setLayoutParams(params);
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorAccent)); // Active Color
        }
    }

    private int currentPage() {
        return viewPager.getCurrentItem();
    }

    void onPageChange(int position){

        addBottomDots(position);

        if (position == layouts.length - 1) {
            next.setText("Submit");
        } else if (position == 0) {
            prev.setVisibility(View.INVISIBLE);
        } else {
            // still pages are left
            next.setText("Next");
            prev.setVisibility(View.VISIBLE);
        }
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            onPageChange(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((Items) pages[3]).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views[position]);
            return views[position];
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    void submit() {

        if (mPref.contains("Source") && mPref.contains("Destination") && mPref.contains("date") && mPref.contains("item_count")) {
            new add_requset().execute();
            startActivity(new Intent(Add_Request_slider.this, Home.class));
        } else {
            Toast.makeText(Add_Request_slider.this, "Please fill all the feilds", Toast.LENGTH_SHORT).show();
        }

    }

    class add_requset extends AsyncTask<Void, Void, Void> {

        Json js;
        String url = IP_ADDRESS + "addRequest.php?uid=" + sp.getId();
        String result;

        @Override
        protected Void doInBackground(Void... voids) {

            js = new Json(Add_Request_slider.this);
            String request = "&s_addr=" + mPref.getString(From + "_addr", "") +
                    "&s_city=" + mPref.getString(From + "_city", "") +
                    "&s_state=" + mPref.getString(From + "_state", "") +
                    "&s_lift=" + mPref.getString(From + "_lift", "") +
                    "&d_addr=" + mPref.getString(To + "_addr", "") +
                    "&d_city=" + mPref.getString(To + "_city", "") +
                    "&d_state=" + mPref.getString(To + "_state", "") +
                    "&d_lift=" + mPref.getString(To + "_lift", "") +
                    "&time=" + mPref.getString("time", "") +
                    "&date=" + mPref.getString("date", "") +
                    "&items=" + mPref.getString("item_count", "") +
                    "&request_loading=" + mPref.getString(From + "_loading", "") + //TODO to_Loading
                    "&home_descr=" + mPref.getString(From + "_house_descr", "") + "%20" + mPref.getString(To + "_house_descr", "");

            url += request.replaceAll(" ", "%20");
            Log.d("Data", url);
            result = js.processURL(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(Add_Request_slider.this, result, Toast.LENGTH_SHORT).show();
//TODO            SharedPreferences.Editor editor = mPref.edit();
//            editor.clear();
//            editor.commit();
            startActivity(new Intent(Add_Request_slider.this, Accepted_Bid.class));
        }
    }

}
