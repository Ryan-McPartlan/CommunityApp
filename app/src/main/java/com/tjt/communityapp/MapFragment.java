package com.tjt.communityapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener{
    private final int PERMISSION_FINE_LOCATION = 1;
    private int PIN_PLACE_MAX_DISTANCE = 15000; //The farthest we can place a pin, in meters
    private int PIN_PLACE_MIN_ZOOM = 15;
    private float PIN_MIN_SPACING = 15; //Meters
    public int MAX_ZOOM = 19;
    public int MIN_ZOOM = 5;

    int pinsInArea; //We use this to keep track of how many pins we see when we geoquery a location we plan to drop a pin on.

    MapView mMapView;
    private GoogleMap gMap;

    //Pins
    private LatLng lastUpdatePosition;
    private float lastUpdateZoom;
    private boolean searchingForPins = false;
    private int queriesInProgress;
    private ArrayList<Marker> pins = new ArrayList<>();
    private ArrayList<Pin> newPins = new ArrayList<>();

    public MapFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_map, container, false);
        getActivity().setTitle("Map");

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

        //The most pixels we see in one direction
        double maxPixels = Math.max(screenHeight, screenWidth);
        double maxDensityPixels = maxPixels / density;

        return (float) (maxDensityPixels * metersPerPixel);
    }

    //Handle Map activity
    //Pin managment
    @Override
    public void onCameraMove() {
            attemptGetPins();
    }
    private void attemptGetPins(){
        //If we are not searching for pins, and we have moved enough, look for pins again
        //Called when camera moves, or when a search finishes.
        if(!searchingForPins){
            float distanceMoved = distanceBetweenLatLngs(lastUpdatePosition, gMap.getCameraPosition().target);
            float distanceToRecalculate = mapSizeMeters(lastUpdateZoom)/3;
            float zoomChange = Math.abs(gMap.getCameraPosition().zoom - lastUpdateZoom);

            if(lastUpdatePosition == null){
                getPins();
            }
            else if( distanceMoved > distanceToRecalculate || zoomChange > 0.5){
                getPins();
            }
        }
    }
    private void getPins(){
        lastUpdateZoom = gMap.getCameraPosition().zoom;
        lastUpdatePosition = gMap.getCameraPosition().target;
        final GeoLocation queryLocation = new GeoLocation(lastUpdatePosition.latitude, lastUpdatePosition.longitude);

        Log.d("MapUpdate", "Updating Pins");

        //Prepare to search for pins
        searchingForPins = true;
        queriesInProgress = 0;

        //TODO: Geohash, get keys for all pins in the area and draw them
        Log.d("GeoHashTest", App.s.geoFire.getDatabaseReference().getKey());
        App.s.geoFire.queryAtLocation(queryLocation, mapSizeMeters(lastUpdateZoom) / 1000).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("GeoHashTest", key);

                queriesInProgress ++;
                App.s.fDatabase.child("pins").orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Log.d("GeoHashTest", "Test2");
                            newPins.add(snapshot.getValue(Pin.class));
                            queriesInProgress--;
                            if(queriesInProgress == 0){
                                placePins();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        queriesInProgress--;
                        if(queriesInProgress == 0){
                            placePins();
                        }
                    }
                });
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("Geohash", "Done!");
                if(queriesInProgress == 0){
                    placePins();
                }
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
    private void placePins(){
        clearPins();
        for (int i = 0; i < newPins.size(); i++){
            placePin(newPins.get(i));
        }
        newPins.clear();
        searchingForPins = false;
        attemptGetPins();
    }
    private void placePin(Pin pin){
        Log.d("GeoHashTest", "test3");
        Marker newMarker = gMap.addMarker(new MarkerOptions()
                .position(new LatLng(pin.lat, pin.lng))
                .title(pin.title));
        newMarker.setTag(pin);
        pins.add(newMarker);
    }
    private void clearPins(){
        //When we search for new pins, we clear the old ones
        for(int i = 0; i < pins.size(); i++){
            pins.get(i).remove();
        }
        pins.clear();
    }

    @Override
    public void onMapClick(final LatLng clickLatlng) {
        //When we click the map, attempt to place a marker.
        // Need to be logged in, have location permissions, be within x distance from our marker, need to be zoomed enough
        final float zoom = gMap.getCameraPosition().zoom;

        App.s.progress("Checking location...", getActivity());

        //Need to be logged in to place marker
        if(App.s.fAuth.getCurrentUser() != null){
            //Must have location permissions to place marker
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    App.s.gLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location userLocation) {
                            final LatLng userLatlng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                            float distance = distanceBetweenLatLngs(userLatlng, clickLatlng);

                            Log.d("Test1", "Distance: " + distance + " Latlng1: " + userLatlng.latitude + " " + userLatlng.longitude + " Latlng2: " + clickLatlng.latitude + " " + clickLatlng.longitude);
                            //Must be within X meters of the pin you are placing
                            if(distance < PIN_PLACE_MAX_DISTANCE){
                                if(zoom >= PIN_PLACE_MIN_ZOOM){
                                    pinsInArea = 0;
                                    //Must be farther than X meters from other pins
                                    App.s.geoFire.queryAtLocation(new GeoLocation(clickLatlng.latitude, clickLatlng.longitude), PIN_MIN_SPACING / 1000f).addGeoQueryEventListener(new GeoQueryEventListener() {
                                        @Override
                                        public void onKeyEntered(String key, GeoLocation location) {
                                            pinsInArea += 1;
                                        }

                                        @Override
                                        public void onGeoQueryReady() {
                                            App.s.dismissProgress();
                                            if(pinsInArea > 0){
                                                App.s.toast("Please place pins at least " + PIN_MIN_SPACING + " meters from any active pins!");
                                            } else{
                                                attemptPlaceMarker(clickLatlng);
                                            }
                                        }

                                        //Unused
                                        @Override
                                        public void onKeyExited(String key) {}
                                        @Override
                                        public void onKeyMoved(String key, GeoLocation location) {}
                                        @Override
                                        public void onGeoQueryError(DatabaseError error) {}
                                    });
                                } else{
                                    App.s.toast("Zoom in to place your event pin more accurately!");
                                }
                            } else{
                                App.s.toast("You can only place pins up to " + PIN_PLACE_MAX_DISTANCE/1000 + " kilometers away!");
                            }
                        }
                    });
                } catch (SecurityException error) {
                    App.s.toast("You must enable location permissions to place pins");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_FINE_LOCATION);
                }
            } else{
                App.s.toast("You must enable location permissions to place pins");
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_FINE_LOCATION);
            }
        } else{
            App.s.toast("You must be logged in to create an event");
        }

        App.s.dismissProgress();
    }
    private void attemptPlaceMarker(LatLng location){
        if(App.s.tempMarker != null)
            App.s.tempMarker.remove();

        //Remember the new pins location so we can use it in createPin
        App.s.newPinCoords = location;

        //Move to the clicked spot, and place a temp marker there
        moveCam(location.latitude, location.longitude, gMap.getCameraPosition().zoom, false);
        App.s.tempMarker = gMap.addMarker(new MarkerOptions().position(location).title("TestMarker"));
        PlacePinDialog placePinDialog = new PlacePinDialog();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        placePinDialog.show(ft, "Test");
    }

    //Map functions
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    public void moveCam(double lat, double lng, float zoom, boolean animate){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom);
        if(animate){
            gMap.animateCamera(update);
        } else{
            gMap.moveCamera(update);
        }
    }

    //Lifecycle functions. Call the map lifecycle functions in here.
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        //Reset our variables
        lastUpdatePosition = new LatLng(0,0);
        lastUpdateZoom = 0;
        searchingForPins = false;
        queriesInProgress = 0;
        pins = new ArrayList<>();
        newPins = new ArrayList<>();
        if(gMap != null){
            getPins();
        }
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
