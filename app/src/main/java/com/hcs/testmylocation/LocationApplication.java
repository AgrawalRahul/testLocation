package com.hcs.testmylocation;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by demon on 02/01/2015.
 */
public class LocationApplication extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static LocationApplication locationApplication;
    private static final int LOCATION_COLLECTION_RATE = 1000 * 3; // milli
    private static final int LOCATION_FASTEST_RATE = 1000 * 2; // milli
    private Location lastLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        locationApplication = this;
        initiateLocationService();
    }

    public static LocationApplication getLocationApplication() {
        return locationApplication;
    }

    private void initiateLocationService() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), this);
        Log.d("location Application", "fetching location");
        if (lastLocation != null) {
            Intent intent = new Intent("my-event");
            intent.putExtra("message", lastLocation.getLatitude() + " - " + lastLocation.getLongitude() + " - " + lastLocation.getAccuracy());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(LOCATION_COLLECTION_RATE);
        request.setFastestInterval(LOCATION_FASTEST_RATE);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("location suspended", "fetching location");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("location failed", "fetching location");
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lastLocation = location;
            Intent intent = new Intent("my-event");
            intent.putExtra("message", lastLocation.getLatitude() + " - " + lastLocation.getLongitude() + " - " + lastLocation.getAccuracy());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
