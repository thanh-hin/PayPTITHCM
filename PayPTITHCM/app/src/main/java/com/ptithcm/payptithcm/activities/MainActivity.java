package com.ptithcm.payptithcm.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.payptithcm.FeeListFragment;
import com.ptithcm.payptithcm.HomeFragment;
import com.ptithcm.payptithcm.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        // Mặc định load HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        nav.setOnNavigationItemSelectedListener(item -> {
            Fragment selected = null;
            if (item.getItemId() == R.id.nav_home) {
                selected = new HomeFragment();
            }
            else if (item.getItemId() == R.id.nav_fees) {
                selected = new FeeListFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
            return true;
        });
    }
}