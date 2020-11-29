package com.example.raternet_isp_app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raternet_isp_app.models.Constants;
import com.example.raternet_isp_app.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LocationService extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;

    // to style map location marker
    private static MarkerOptions markerOptions;
    private Marker markerYourLocation;

    // location settings enabled
    private boolean settingsEnabled = false;
    private static GoogleMap googleMap;
    private Geocoder geocoder;

    // callback to update location after specified interval
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            try {
                updatePosition(lastLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // permission to access location
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12; // passed back to you on completion to differentiate on request from other

    // a boolean variable to prevent generation of multiple markers (marker for each updated location)
    private static boolean markerSetFlag = true;

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.location_view, container, false);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        //LocView = view.findViewById(R.id.location);
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    void updatePosition(Location location) throws IOException {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        //Geocoder geocoder = new Geocoder(getContext());

        try {
            //remove marker of old location
            if(markerYourLocation!=null){
                markerYourLocation.remove();
            }

           LatLng coordinates = new LatLng(latitude,longitude);

            markerOptions = new MarkerOptions().position(coordinates).title("Current Position")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerYourLocation = googleMap.addMarker(markerOptions);

            // set new marker on updated location and set markerSetFlag to false
            if(markerSetFlag){
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(coordinates)
                        .zoom(18)
                        .bearing(location.getBearing())
                        .tilt(70)
                        .build();
                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                googleMap.animateCamera(camUpd3);
                markerSetFlag = false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        geocoder = new Geocoder(getContext());
        Address address = geocoder.getFromLocation(
                latitude,
                longitude,
                1).get(0);
        //Setting MAP_Latitude and Longitude
        Constants.MAP_Latitude = latitude.toString();
        Constants.MAP_Longitude = longitude.toString();
        Constants.city = address.getLocality();
        Constants.locality = address.getSubLocality();
        String displayAddress = address.getSubLocality()+ " " + address.getLocality() + " " + address.getPostalCode();
        Constants.address = displayAddress;

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        markerSetFlag = true;
    }

    @Override
    public void onStop() {
        stopLocationUpdates();
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    startLocationUpdates();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //check if location settings are enabled on device
    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                settingsEnabled = true;
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    settingsEnabled = false;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                this.locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        checkLocationSettings();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && settingsEnabled) {
            // if user has already given permission
            startLocationUpdates();
        } else {
            requestPermissions(LOCATION_PERMS, MY_PERMISSION_ACCESS_FINE_LOCATION); // request to allow location
            // request results are returned in onRequestPermissionsResult function
        }

    }
}
