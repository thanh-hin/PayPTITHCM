package com.ptithcm.payptithcm.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.payptithcm.FeeListFragment;
import com.ptithcm.payptithcm.HistoryFragment;
import com.ptithcm.payptithcm.HomeFragment;
import com.ptithcm.payptithcm.ProfileFragment;
import com.ptithcm.payptithcm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        // Mac dinh load HomeFragment
        loadFragment(new HomeFragment());

        nav.setOnNavigationItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_fees) {
                selected = new FeeListFragment();
            } else if (id == R.id.nav_history) {
                selected = new HistoryFragment();
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            } else {
                selected = new HomeFragment();
            }
            loadFragment(selected);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
