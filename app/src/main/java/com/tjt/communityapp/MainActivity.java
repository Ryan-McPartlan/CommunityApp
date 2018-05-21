package com.tjt.communityapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.menu.MenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    final int PERMISSION_COARSE_LOCATION = 1;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.s.currentActivity = this;

        navBarSetup();

        //Start with the map open, set it checked in the NavDrawer
        changeFragment(new MapFragment(), true);

        //If we don't have location permissions
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_COARSE_LOCATION);
    }

    //Setup the navbar. Must be called by nav drawer activities after they set their content view
    protected void navBarSetup(){
        drawer = findViewById(R.id.drawer_layout);

        //Add the drawer button to the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Handle clicking a drawer button
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Handles us clicking NavBar items
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_maps) {
            changeFragment(new MapFragment(), true);
        } else if (id == R.id.nav_personal) {
            changeFragment(new PersonalPageFragment(), true);
        } else if (id == R.id.nav_communities) {
            changeFragment(new CommunitiesFragment(), true);
        } else if (id == R.id.nav_filters) {
            changeFragment(new FiltersFragment(), true);
        }else if (id == R.id.nav_options) {
            changeFragment(new OptionsFragment(), true);
        }else if (id == R.id.nav_news) {
            changeFragment(new NewsFragment(), true);
        }else if (id == R.id.nav_logout) {
            App.s.logout();
        }

        return true;
    }

    //Swaps out the fragment we are seeing
    public void changeFragment(Fragment fragment, boolean resetStack){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(resetStack){
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }
        ft.addToBackStack(null);

        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
    private void addFragment(Fragment fragment){

    }

    //When we press back, close the drawer, if it is closed, logout.
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount() == 1){
            App.s.logout();
        } else {
            super.onBackPressed();
        }
    }
}
