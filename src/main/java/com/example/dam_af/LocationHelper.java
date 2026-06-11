package com.example.dam_af;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {
    private Context context;
    private FusedLocationProviderClient fusedClient;

    public LocationHelper(Context context) {

        this.context = context;

        fusedClient =
                LocationServices
                        .getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(
            PlaceAdapter.LocationCallback callback) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {
                        callback.onLocationReceived(location);
                    }
                });
    }
}


