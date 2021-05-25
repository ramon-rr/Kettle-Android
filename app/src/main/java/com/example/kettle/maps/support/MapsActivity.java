 package com.example.kettle.maps.support;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.kettle.KettleViewModel;
import com.example.kettle.PostInfo;
import com.example.kettle.R;
import com.example.kettle.networking.HttpPostRequest;
import com.example.kettle.ui.fragments.PostFragment;
import com.example.kettle.ui.fragments.PostViewFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ramon Reyes
 * Desc: Google Maps Activity that is the entry point of User Interaction
 * pinning, viewing profile, messaging is accessible through this class
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    ArrayList<LatLng> allPoints = new ArrayList<>();
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    KettleViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        model = new ViewModelProvider(this).get(KettleViewModel.class);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    //TODO Needs to GET all the posts in the area to be able to load them in to the map at an interval
    // Radius calculation will be needed to find what posts to get
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;



        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Disable scroll map fragment
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        //Disable zoom map fragment
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(12000); // two minute interval
        mLocationRequest.setFastestInterval(12000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        checkLocationPermission();

       /**OnClick Listener places pins on the map and saves them to the allPoints ArrayList
         *
         * */
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                allPoints.add(point);
                PostFragment pf = new PostFragment();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.map, pf).addToBackStack("PostFragment").commit();
            }
        });

        final Observer<Boolean> postCreatedObserver = new Observer<Boolean>() {
            String postTitle = "";
            String postBody = "";
            @Override
            public void onChanged(Boolean created) {
                if(created){
                    GroundOverlay postGroundOverlay = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                            .image(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                            .position(allPoints.get(allPoints.size()-1), 15f, 15f)
                            .clickable(true));

                    postTitle = model.getPostTitle().getValue();
                    postBody = model.getPostBody().getValue();
                    HttpPostRequest postRequest = new HttpPostRequest();
                    postRequest.execute("TestUser", postTitle, postBody);
                    PostInfo pi = new PostInfo("TestUser", postTitle, postBody);
                    postGroundOverlay.setTag(pi);
                    mGoogleMap.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener(){
                        /**
                         * Sets the onClick listener to display the post information for this pin created
                         * by getting the GroundOverlay tag that was set when the post was created
                         */
                        @Override
                        public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                            PostInfo retrievedPi = (PostInfo) groundOverlay.getTag();
                            System.out.println("** " + retrievedPi.getUser()+ " " + retrievedPi.getTitle() +
                                    " " + retrievedPi.getBody());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("VIEW_POST", retrievedPi);
                            PostViewFragment pvm = new PostViewFragment();
                            pvm.setArguments(bundle);
                            FragmentManager fm = getSupportFragmentManager();
                            fm.beginTransaction().replace(R.id.map, pvm).addToBackStack("PostViewFragment").commit();
                        }
                    });
                    model.getPostCreated().setValue(false);
                }
            }
        };
        model.getPostCreated().observe(this, postCreatedObserver);


        mGoogleMap.setOnCameraMoveStartedListener(new  GoogleMap.OnCameraMoveStartedListener(){
            @Override
            public void onCameraMoveStarted(int i) {
                LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
                LatLng northeast = bounds.northeast;
                LatLng southwest = bounds.southwest;

                Context context = getApplicationContext();
                CharSequence text = "ne:"+northeast+" sw:"+southwest;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }


    /**
     * Call back for user location change. This updates the list of locations at an interval
     * last location is the last element in the list, where the user was located.
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() +
                        " " + location.getLongitude());

                mLastLocation = location;

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker DO NOT USE EXAMPLE FOR MARKER PLACEMENT
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);*/

                //move map camera
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                System.out.println("Camera set to user**********");
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }
        }
    };

    /**
     * Checks for location permissions, if it was not granted, it requests location permissions
     * by calling requestLocationPermission()
     */
    private void checkLocationPermission(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                requestLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Requests location permission by showing the user a popup which contain call backs to
     * set permissions in the manifest according to user input. Will notify the user that the app
     * needs permissions if the choose to deny location permissions.
     */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}