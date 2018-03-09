package com.example.gptsi.roadie.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.gptsi.roadie.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class locationServices extends Service implements LocationListener {

    private Context mContext;
    private locationServicesCallback mCallback;

    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;

    protected LocationManager locationManager;
    Location mLocation = null;

    private static final long SECONDS = 1000;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10; //meters
    private static final long MIN_TIME_FOR_UPDATE = 1 * SECONDS; //milliseconds
    public static int LOCATION_REQUEST_CODE = 99;
    private static String TAG = locationServices.class.getSimpleName();

    public static final int GPS = 0;
    public static final int NETWORK = 1;
    public static final int GEOLOCATION = 2;
    private int mProvider = NETWORK;

    getDirectionsAPI mGDirApi;

    @SuppressLint("NewApi")
    public locationServices(Context context,int provider, locationServicesCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        mProvider = provider;
        mGDirApi = new getDirectionsAPI();
        // TODO Battery Saver Mode

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissionl2s
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Does not have location permisssion");
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            canGetLocation = true;
            Log.d(TAG, "has location permisssion");
        }

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        mLocation = new Location(LocationManager.GPS_PROVIDER);
        mLocation.setLatitude(22.307);
        mLocation.setLongitude(73.1812);
        getLocation();
    }

    public Location getLocation() {

        try {
            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {

                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();

            } else {
                this.canGetLocation = true;

                if(mProvider == GPS) {
                    // if GPS Enabled get lat/long using GPS Services
                    if (checkGPS) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return mLocation;
                        }
                        if (locationManager != null) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_FOR_UPDATE, this);

                            mLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            Toast.makeText(mContext, "GPS", Toast.LENGTH_SHORT).show();

                            if (mLocation != null) {
                                return mLocation;
                            }
                        }
                    }
                }

                if(mProvider == NETWORK) {

                    if (checkNetwork) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return mLocation;
                        }
                        if (locationManager != null) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_FOR_UPDATE,
                                    MIN_DISTANCE_FOR_UPDATE, this);

                            mLocation = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Toast.makeText(mContext, "NETWORK", Toast.LENGTH_SHORT).show();
                        }

                        if (mLocation != null) {
                            return mLocation;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;
        mCallback.onLocationChanged(mLocation);
        Log.d(TAG,"on location change called");
    }

    public int getProvider() {
        return mProvider;
    }

    public void setProvider(int provider) {
        this.mProvider = provider;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public static class locationAddress {
        String address,city,state,country,knownName,postalCode;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getKnownName() {
            return knownName;
        }

        public void setKnownName(String knownName) {
            this.knownName = knownName;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }

    public locationAddress getAddress(Location location){
        Geocoder geocoder;
        locationAddress addr = new locationAddress();
        List<Address> address;
        geocoder = new Geocoder(mContext, Locale.ENGLISH);

        try {

            address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addr.setAddress(address.get(0).getAddressLine(0)); // If any additional address line present than only, check with max available locationAddress lines by getMaxAddressLineIndex()
            addr.setCity(address.get(0).getLocality());
            addr.setState(address.get(0).getAdminArea());
            addr.setCountry(address.get(0).getCountryName());
            addr.setPostalCode(address.get(0).getPostalCode());
            addr.setKnownName(address.get(0).getFeatureName()); // Only if available else return NULL

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addr;
    }

    public void stopListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d(TAG,"Stop Updates");
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundlke) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public directionUtil getDirections(LatLng source,LatLng dest){

        return mGDirApi.directions(source, dest);
    }

   public class directionUtil{

       private timeUtil mTime;           //till destination
       private distanceUtil mDistance;       //till destination
       private PolylineOptions mPolyline;

       public timeUtil getTime() {
           return mTime;
       }

       public void setTime(timeUtil mTime) {
           this.mTime = mTime;
       }

       public distanceUtil getDistance() {
           return mDistance;
       }

       public void setDistance(distanceUtil mDistance) {
           this.mDistance = mDistance;
       }

       public PolylineOptions getPolyline() {
           return mPolyline;
       }

       public void setPolyline(PolylineOptions mPolyline) {
           this.mPolyline = mPolyline;
       }
   }

    public class getDirectionsAPI {

        directionUtil direction = new directionUtil();

        @SuppressLint("NewApi")
        directionUtil directions(LatLng source, LatLng dest) {

            com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(source.latitude, source.longitude);
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(dest.latitude, dest.longitude);

            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(mContext.getString(R.string.google_maps_api_key))
                    .build();

            try {

                // - Perform the actual request
                DirectionsResult directionsResult;
                long duration=0;
                long distance=0;

                ArrayList<LatLng> routes = new ArrayList<>();

                directionsResult = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(origin)
                        .destination(destination)
                        .await();

                for (int l = 0; l < directionsResult.routes.length; l++) {
                    // - Parse the result
                    DirectionsRoute route = directionsResult.routes[l];

                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        for (int j = 0; j < leg.steps.length; j++) {
                            DirectionsStep step = leg.steps[j];
                            List<com.google.maps.model.LatLng> list = step.polyline.decodePath();
                            for (int k = 0; k < list.size(); k++) {
                                LatLng latLng = new LatLng(list.get(k).lat, list.get(k).lng);
                                routes.add(latLng);
                            }
                        }
                        distance+=leg.distance.inMeters;
                        duration += leg.duration.inSeconds;
                    }
                }

                direction.setTime(new timeUtil(duration));
                direction.setDistance(new distanceUtil(distance));

                // Drawing polyline in the Google Map for the i-th route
                PolylineOptions mPolyline = new PolylineOptions();
                mPolyline.addAll(routes).width(15).color(mContext.getColor(R.color.colorPrimary)).geodesic(true);
                direction.setPolyline(mPolyline);

                return direction;

            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class timeUtil {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;
        public long mSeconds;
        public long mMinutes;
        public long mHours;
        private String mTime="";

        public timeUtil(long seconds) {

            mSeconds = seconds % SECONDS_IN_A_MINUTE;
            long minutes = seconds / SECONDS_IN_A_MINUTE;
            mMinutes = minutes % MINUTES_IN_AN_HOUR;
            mHours = minutes / MINUTES_IN_AN_HOUR;
        }

        public String humanReadable() {
            mTime="";

            if(mHours>0)
                mTime = mHours + ((mHours>1) ? " hrs " : " hr ");

            if(mMinutes>1)
                mTime += mMinutes + " mins ";
            else
                mTime = "1 min";

            //            mTime += mSeconds + " secs ";

            return  mTime;
        }

        public long getSeconds() {
            return mSeconds;
        }

        public long getMinutes() {
            return mMinutes;
        }

        public long getHours() {
            return mHours;
        }
    }

    public class distanceUtil{

        final int METERS_IN_KILOMETER = 1000;
        public double mKMeters;
        public long mMeters;
        private String mDistance="";

        public distanceUtil(long meters) {

            mMeters = meters % METERS_IN_KILOMETER;
            mKMeters = ((double)meters) / METERS_IN_KILOMETER;
        }

        public String humanReadable() {

            if(mKMeters>0) {
                mDistance = String.format("%.1f Km",mKMeters);
            }
            else
                mDistance = mMeters + " meters ";

            return  mDistance;
        }

        public double getKMeters() {
            return mKMeters;
        }

        public long getMeters() {
            return mMeters;
        }
    }
}