package com.example.gptsi.roadie.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.locationServices;
import com.example.gptsi.roadie.Util.locationServicesCallback;
import com.example.gptsi.roadie.Util.wakeLock;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.function.Function;

import static com.example.gptsi.roadie.Util.locationServices.GPS;
import static com.example.gptsi.roadie.Util.locationServices.LOCATION_REQUEST_CODE;
import static com.example.gptsi.roadie.Util.locationServices.NETWORK;

public class Track_ServiceProvider extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    Toolbar toolbar;
    locationServices mLocation;
    BottomNavigationView navigation;
    TextView time, distance;
    locationServicesCallback callback;
    int mZoom = 0;
    Location mCurrentLocation;
    wakeLock wakelock;
    String TAG = Track_ServiceProvider.class.getSimpleName();
    //TODO: Before you run your application, you need a Google Maps API key.

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_service_provider);

        toolbar = (Toolbar) findViewById(R.id.appbar);

        toolbar.setTitle("Track");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        callback = new locationServicesCallback() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Track Location change called");
                mCurrentLocation = location;
                updateLocation(location);
            }
        };

        mLocation = new locationServices(Track_ServiceProvider.this, NETWORK, callback);
        wakelock = new wakeLock(Track_ServiceProvider.this);
        wakelock.activate();

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        time = navigation.findViewById(R.id.track_time);
        distance = navigation.findViewById(R.id.track_Distance);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listenerl2s or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mCurrentLocation = mLocation.getLocation();
        updateLocation(mCurrentLocation);
    }

    void updateLocation(Location loc) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || mMap == null) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());

        LatLng dst;
        dst = new LatLng(22.297066, 73.133582);

        locationServices.directionUtil direction = mLocation.getDirections(location, dst);
        mMap.clear();
        mMap.addPolyline(direction.getPolyline());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        if (mZoom<9)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        locationServices.locationAddress address;
        address = mLocation.getAddress(loc);
        time.setText(direction.getTime().humanReadable());
        distance.setText(direction.getDistance().humanReadable());
        if(direction.getDistance().getMeters()<200){
            //TODO on receive data from SP APP
            Intent i = new Intent(Track_ServiceProvider.this, RateServiceProvider.class);
//            i.putExtra("images",mList.get(position).getItems());
            startActivity(i);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mCurrentLocation = mLocation.getLocation();
            updateLocation(mCurrentLocation);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateLocation(mLocation.getLocation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation(mLocation.getLocation());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocation.stopListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocation.stopListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakelock.deactivate();
    }
}