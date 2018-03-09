package com.example.gptsi.roadie.Activity;

import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.Fragment.Homepage;
import com.example.gptsi.roadie.Fragment.SP_Homepage;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.SQLite;
import com.example.gptsi.roadie.Util.address_autofill;
import com.example.gptsi.roadie.Util.shared_preferences;
import com.example.gptsi.roadie.Util.socialmediaIntegration.facebookSignIn;
import com.example.gptsi.roadie.Util.socialmediaIntegration.googleSignIn;
import com.example.gptsi.roadie.Util.touchCallback;
import com.example.gptsi.roadie.Util.touch_util;

import java.util.ArrayList;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    shared_preferences sp;
    String semail,sid,sname,profile_pic;
    String TAG = Home.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        SearchView search = findViewById(R.id.search_view);
        LinearLayout layout = findViewById(R.id.home_layout);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "New request", Snackbar.LENGTH_LONG)
                        .setAction("Create", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Home.this,Add_Request_slider.class));
                            }
                        }).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //TODO remove
        startActivity(new Intent(Home.this,ChatFB.class));


//        Temp t = new Temp();
//        SQLite sqLite = new SQLite(Home.this,"DB",1,"reg",t);
//        Temp t1 = new Temp(1);
//        sqLite.insert(t1);
//        ArrayList<Object> tlist = sqLite.getdata();
//        Log.d("SQLite",""+((Temp)tlist.get(0)).i1);
























        final Homepage h=new Homepage();
        android.support.v4.app.FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_layout,h);
        ft.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sp = new shared_preferences(this);
        sid=sp.getId();
        semail=sp.getEmail();
        sname=sp.getName();
        profile_pic=sp.getPhoto();

        View v=navigationView.getHeaderView(0);
        TextView name=(TextView)v.findViewById(R.id.nav_name);
        TextView email=(TextView)v.findViewById(R.id.nav_email);
        ImageView photo=(ImageView)v.findViewById(R.id.nav_photo);

        name.setText(sname);
        email.setText(semail);
        Glide.with(Home.this).load(profile_pic).into(photo);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                Log.d(TAG,newText+" Len: "+newText.length());
                h.adapter.filter(text);
                return false;
            }
        });


        touchCallback callback = new touchCallback() {
            @Override
            public void onSwipeRight() {
                SP_Homepage h=new SP_Homepage();
                android.support.v4.app.FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.home_layout,h);
                ft.commit();
            }

            @Override
            public void onSwipeLeft() {
                Homepage h=new Homepage();
                android.support.v4.app.FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.home_layout,h);
                ft.commit();
            }

            @Override
            public void onSwipeTop() {

            }

            @Override
            public void onSwipeBottom() {

            }
        };

        layout.setOnTouchListener(new touch_util(Home.this,callback));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            facebookSignIn.signOut();
            googleSignIn.signOut();
            sp.clearPreferences();
            startActivity(new Intent(Home.this,Login.class));
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            startActivity(new Intent(Home.this,Account.class));
        } else if (id == R.id.nav_add_request) {
            startActivity(new Intent(Home.this,Add_Request_slider.class));
        } else if (id == R.id.nav_bid) {
            startActivity(new Intent(Home.this,View_Bid.class));
        } else if (id == R.id.nav_favourites) {
            startActivity(new Intent(Home.this,Favourites.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(Home.this,History.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
