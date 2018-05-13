package com.tjt.communityapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener{

    MapView mMapView;
    private GoogleMap gMap;
    public int MAX_ZOOM = 19;
    public int MIN_ZOOM = 5;

    public MapFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_map, container, false);

        //Set the title in our top bar to 'Map'
        getActivity().setTitle("Map");

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        //gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle)); TODO: Create a map style
        moveCam(41, 74, 6, false); //TODO: Handle start location
    }
    @Override
    public void onCameraMove() {

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
