package com.ptithcm.payptithcm.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.payptithcm.FeeListFragment;
import com.ptithcm.payptithcm.HistoryFragment;
import com.ptithcm.payptithcm.HomeFragment;
import com.ptithcm.payptithcm.ProfileFragment;
import com.ptithcm.payptithcm.SupportFragment;
import com.ptithcm.payptithcm.R;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_HOME    = 0;
    private static final int TAB_FEES    = 1;
    private static final int TAB_HISTORY = 2;
    private static final int TAB_PROFILE = 3;
    private static final int TAB_SUPPORT = 4;

    ViewPager2 viewPager;
    BottomNavigationView bottomNav;
    private boolean isNavigating = false;

    private final int[] NAV_IDS = {
        R.id.nav_home, R.id.nav_fees, R.id.nav_history,
        R.id.nav_profile, R.id.nav_support
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottom_navigation);

        viewPager.setAdapter(new MainPagerAdapter(this));
        // Giữ tất cả fragment trong bộ nhớ để không bị reset khi swipe
        viewPager.setOffscreenPageLimit(4);

        // Sync ViewPager → BottomNav
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (!isNavigating) {
                    isNavigating = true;
                    bottomNav.setSelectedItemId(NAV_IDS[position]);
                    isNavigating = false;
                }
            }
        });

        // Sync BottomNav → ViewPager
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int tab = tabFromNavId(id);
            if (tab >= 0 && !isNavigating) {
                isNavigating = true;
                viewPager.setCurrentItem(tab, true);
                isNavigating = false;
            }
            return true;
        });
    }

    private int tabFromNavId(int navId) {
        if (navId == R.id.nav_home)    return TAB_HOME;
        if (navId == R.id.nav_fees)    return TAB_FEES;
        if (navId == R.id.nav_history) return TAB_HISTORY;
        if (navId == R.id.nav_profile) return TAB_PROFILE;
        if (navId == R.id.nav_support) return TAB_SUPPORT;
        return -1;
    }

    /** Public method cho các fragment gọi chuyển tab */
    public void navigateTo(int navItemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(navItemId);
        }
    }

    // ===== Adapter =====
    static class MainPagerAdapter extends FragmentStateAdapter {
        MainPagerAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public int getItemCount() { return 5; }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case TAB_FEES:    return new FeeListFragment();
                case TAB_HISTORY: return new HistoryFragment();
                case TAB_PROFILE: return new ProfileFragment();
                case TAB_SUPPORT: return new SupportFragment();
                default:          return new HomeFragment();
            }
        }
    }
}
