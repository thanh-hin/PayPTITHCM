package com.ptithcm.payptithcm.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ptithcm.payptithcm.FeeListFragment;
import com.ptithcm.payptithcm.HistoryFragment;
import com.ptithcm.payptithcm.HomeFragment;
import com.ptithcm.payptithcm.ProfileFragment;
import com.ptithcm.payptithcm.R;

public class MainActivity extends AppCompatActivity {

    // Tags de tim lai fragment cu, khong tao moi moi lan bam nav
    private static final String TAG_HOME    = "HOME";
    private static final String TAG_FEES    = "FEES";
    private static final String TAG_HISTORY = "HISTORY";
    private static final String TAG_PROFILE = "PROFILE";

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        // Chi tao fragment lan dau, sau do show/hide de giu trang thai
        if (savedInstanceState == null) {
            showFragment(TAG_HOME, null);
        } else {
            // Khi xoay man hinh: phuc hoi fragment dang hien
            String activeTag = savedInstanceState.getString("ACTIVE_FRAGMENT", TAG_HOME);
            activeFragment = getSupportFragmentManager().findFragmentByTag(activeTag);
            if (activeFragment == null) {
                showFragment(TAG_HOME, null);
            }
        }

        // setOnItemSelectedListener - API moi thay cho deprecated setOnNavigationItemSelectedListener
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showFragment(TAG_HOME, null);
            } else if (id == R.id.nav_fees) {
                showFragment(TAG_FEES, null);
            } else if (id == R.id.nav_history) {
                showFragment(TAG_HISTORY, null);
            } else if (id == R.id.nav_profile) {
                showFragment(TAG_PROFILE, null);
            }
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment != null) {
            outState.putString("ACTIVE_FRAGMENT", activeFragment.getTag());
        }
    }

    /**
     * Hien fragment theo tag. Neu chua co thi tao moi va add vao back stack.
     * Neu da co thi chi show lai (giu nguyen trang thai).
     */
    private void showFragment(String tag, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // An fragment hien tai
        if (activeFragment != null) {
            ft.hide(activeFragment);
        }

        Fragment target = fm.findFragmentByTag(tag);
        if (target == null) {
            // Tao moi lan dau
            target = createFragment(tag);
            if (args != null) target.setArguments(args);
            ft.add(R.id.fragment_container, target, tag);
        } else {
            ft.show(target);
        }

        activeFragment = target;
        ft.commit();
    }

    private Fragment createFragment(String tag) {
        switch (tag) {
            case TAG_FEES:    return new FeeListFragment();
            case TAG_HISTORY: return new HistoryFragment();
            case TAG_PROFILE: return new ProfileFragment();
            default:          return new HomeFragment();
        }
    }

    /**
     * Public method de cac fragment goi chuyen tab tu ben ngoai (vi du: HomeFragment)
     */
    public void navigateTo(int navItemId) {
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (nav != null) {
            nav.setSelectedItemId(navItemId);
        }
    }
}
