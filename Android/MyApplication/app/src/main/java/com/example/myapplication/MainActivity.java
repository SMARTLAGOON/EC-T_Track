package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView botNav;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        botNav = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        ChartFragment chartFragment = new ChartFragment();
        MapFragment mapFragment = new MapFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        StyleFragment styleFragment = new StyleFragment();
        CharECFragment chartECFragment = new CharECFragment();
        setCurrentFragment(settingsFragment);

        this.setTitle("Sonda 2.0");
        botNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.nav_chart:
                        setCurrentFragment(chartFragment);
                        return true;
                    case R.id.nav_chart2:
                        setCurrentFragment(chartECFragment);
                        return true;
                    case R.id.nav_places:
                        setCurrentFragment(mapFragment);
                        return true;
                    case R.id.nav_settings:
                        setCurrentFragment(settingsFragment);
                        return true;
                    case R.id.nav_style:
                        setCurrentFragment(styleFragment);
                        return true;
                }
                return false;
            }
        });
    }
    private void setCurrentFragment(Fragment fr){
        FragmentManager fraMan = getSupportFragmentManager();
        FragmentTransaction fraTra = fraMan.beginTransaction();
        fraTra.replace(R.id.containerView, fr);
        fraTra.commit();
    }
}