package com.tjt.communityapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navBarSetup();
        changeFragment(new MapActivity());
    }

    //Setup the navbar. Must be called by all navActivies after they set their content view
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

    //Set a nav bar option to checked. Called when we inflate the navbar based on current activity
    protected void setNavOptionChecked(int itemId){
        MenuView.ItemView view = (MenuView.ItemView) findViewById(itemId);
        view.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        item.setChecked(true);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_maps) {
            changeFragment(new MapActivity());
        } else if (id == R.id.nav_personal) {
            changeFragment(new PersonalPageActivity());
        } else if (id == R.id.nav_options) {
            changeFragment(new OptionsActivity());
        }

        return true;
    }

    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}
