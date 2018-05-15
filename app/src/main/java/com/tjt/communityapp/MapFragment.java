package com.tjt.communityapp;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener{

    private final int PERMISSION_FINE_LOCATION = 1;

    MapView mMapView;
    private GoogleMap gMap;
    private GeoFire geoFire;
    private boolean gettingPins = false;

    public int MAX_ZOOM = 19;
    public int MIN_ZOOM = 5;

    //For checking when we should findPins again
    private LatLng lastUpdatePosition;
    private float lastUpdateZoom;


    //Pins
    private ArrayList<Pin> currentPins = new ArrayList<>();
    private ArrayList<Pin> newPins = new ArrayList<>();

    public MapFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_map, container, false);

        //Set the title in our top bar to 'Map'
        getActivity().setTitle("Map");

        //Setup GeoFire
        geoFire = new GeoFire(App.s.fDatabase.child("geoFire"));

        //Get the mapView
        mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //If we don't have location permissions, ask for permission.
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_FINE_LOCATION);

        //Create the map
        mMapView.getMapAsync(this);

        return rootView;
    }

    //MAP FUNCTIONS
    @Override
    public void onMapReady(GoogleMap mMap) {
        //Setup the google map
        gMap = mMap;
        gMap.setMinZoomPreference(MIN_ZOOM);
        gMap.setMaxZoomPreference(MAX_ZOOM);
        gMap.getUiSettings().setTiltGesturesEnabled(false); //TODO: Option to allow tilt
        gMap.getUiSettings().setRotateGesturesEnabled(false); //TODO: Option to allow rotation
        gMap.setOnMapClickListener(this);
        gMap.setOnCameraMoveListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(App.s, R.raw.mapstyle));

        //Show the 'blue dot' if we have location permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        }

        moveCameraToStartPosition();
    }

    private void moveCameraToStartPosition(){
        //If we have location permissions, look at the users location. Otherwise, start without location
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            App.s.gLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        moveCam((float)location.getLatitude(), (float)location.getLongitude(), 14, false);
                        getPins();
                    } else{
                        startWithoutLocation();
                    }
                }
            });
        } else {
            startWithoutLocation();
        }
    }
    private void startWithoutLocation(){
        moveCam(40.93f, -73.8f, 8, false); //TODO: Prompt the user for a city or zip code.
    }

    private void getPins(){
        lastUpdateZoom = gMap.getCameraPosition().zoom;
        lastUpdatePosition = gMap.getCameraPosition().target;
        GeoLocation queryLocation = new GeoLocation(lastUpdatePosition.latitude, lastUpdatePosition.longitude);

        Log.d("MapUpdate", "Updating Pins");

        gettingPins = true;
        //TODO: Geohash, get keys for all pins in the area and draw them
        geoFire.queryAtLocation(queryLocation, mapSizeMeters(lastUpdateZoom) / 1000).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

            }


            @Override
            public void onGeoQueryReady() {

            }

            //Unused
            @Override
            public void onKeyExited(String key) {}
            @Override
            public void onKeyMoved(String key, GeoLocation location) {}
            @Override
            public void onGeoQueryError(DatabaseError error) {}
        });
    }

    //Camera utilities
    private float distanceBetweenLatLngs(LatLng point1, LatLng point2){
        Location location1 = new Location("");
        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        Location location2 = new Location("");
        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return  location1.distanceTo(location2);
    }
    public float mapSizeMeters(float zoomLevel){
        final double EQUATOR_METERS = 40075004;
        final double DEFAULT_PIXELS = 256;

        //Get the height/width of screen in pixels
        float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        //Get the density of the pixels
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;

        //At zoom level 1, the earth takes up 256 pixels. With each zoom level, this doubles
        double totalPixels = DEFAULT_PIXELS * Math.pow(2, zoomLevel);

        //The equator of the earth divided over the number of pixels it takes up at this zoom level
        double metersPerPixel = EQUATOR_METERS / totalPixels;

        //The most pixels we see in one direction, t
        double maxPixels = Math.max(screenHeight, screenWidth);
        double maxDensityPixels = maxPixels / density;

        return (float) (maxDensityPixels * metersPerPixel);
    }

    //Handle Map activity
    @Override
    public void onCameraMove() {
        float distanceMoved = distanceBetweenLatLngs(lastUpdatePosition, gMap.getCameraPosition().target);
        float distanceToRecalculate = mapSizeMeters(lastUpdateZoom)/3;
        float zoomChange = Math.abs(gMap.getCameraPosition().zoom - lastUpdateZoom);

        Log.d("MapUpdate", Float.toString(distanceMoved));
        Log.d("MapUpdate", Float.toString(distanceToRecalculate));

        if(lastUpdatePosition == null){
            getPins();
        }
        else if( distanceMoved > distanceToRecalculate || zoomChange > 0.5){
            getPins();
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    public void moveCam(float lat, float lng, float zoom, boolean animate){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
        if(animate){
            gMap.animateCamera(update);
        } else{
            gMap.moveCamera(update);
        }
    }

    //Lifecycle functions
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
