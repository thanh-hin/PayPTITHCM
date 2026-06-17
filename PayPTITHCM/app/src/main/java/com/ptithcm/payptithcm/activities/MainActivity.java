package com.ptithcm.payptithcm.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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
import com.ptithcm.payptithcm.InfoFragment;
import com.ptithcm.payptithcm.ProfileFragment;
import com.ptithcm.payptithcm.SupportFragment;
import com.ptithcm.payptithcm.R;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public static final int TAB_FEES    = 0;
    public static final int TAB_HOME    = 1;
    public static final int TAB_PROFILE = 2;
    public static final int TAB_HISTORY = 3;
    public static final int TAB_SUPPORT = 4;
    public static final int TAB_INFO    = 5;

    ViewPager2 viewPager;
    BottomNavigationView bottomNav;
    ImageButton btnBackHeader;
    
    private boolean isNavigating = false;
    private final Stack<Integer> navigationStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottom_navigation);
        btnBackHeader = findViewById(R.id.btnBackHeader);

        viewPager.setAdapter(new MainPagerAdapter(this));
        
        // 1. Vô hiệu hóa vuốt tay
        viewPager.setUserInputEnabled(false);
        
        // 2. Loại bỏ over-scroll
        if (viewPager.getChildAt(0) != null) {
            viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        
        viewPager.setOffscreenPageLimit(4);

        // Sync ViewPager -> BottomNav + Quản lý nút Back
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Hiển thị nút Back nếu không ở trang Home
                btnBackHeader.setVisibility(position == TAB_HOME ? View.GONE : View.VISIBLE);

                if (isNavigating) return;
                isNavigating = true;
                if (position == TAB_FEES) bottomNav.setSelectedItemId(R.id.nav_fees);
                else if (position == TAB_HOME) bottomNav.setSelectedItemId(R.id.nav_home);
                else if (position == TAB_PROFILE) bottomNav.setSelectedItemId(R.id.nav_profile);
                isNavigating = false;
            }
        });

        // Sync BottomNav -> ViewPager
        bottomNav.setOnItemSelectedListener(item -> {
            if (isNavigating) return true;
            int id = item.getItemId();
            int targetTab = -1;
            if (id == R.id.nav_fees) targetTab = TAB_FEES;
            else if (id == R.id.nav_home) targetTab = TAB_HOME;
            else if (id == R.id.nav_profile) targetTab = TAB_PROFILE;
            
            if (targetTab != -1 && targetTab != viewPager.getCurrentItem()) {
                pushToStack(viewPager.getCurrentItem());
                isNavigating = true;
                viewPager.setCurrentItem(targetTab, false);
                isNavigating = false;
            }
            return true;
        });

        // Xử lý sự kiện nhấn nút Trở về
        btnBackHeader.setOnClickListener(v -> {
            if (!navigationStack.isEmpty()) {
                int lastTab = navigationStack.pop();
                isNavigating = true;
                viewPager.setCurrentItem(lastTab, false);
                isNavigating = false;
            } else {
                // Nếu stack trống, mặc định về Home
                isNavigating = true;
                viewPager.setCurrentItem(TAB_HOME, false);
                isNavigating = false;
            }
        });

        // Mặc định vào trang Home
        viewPager.setCurrentItem(TAB_HOME, false);
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void pushToStack(int tabIndex) {
        // Tránh lưu trùng lặp liên tiếp hoặc lưu trang Home vào stack quá nhiều
        if (navigationStack.isEmpty() || navigationStack.peek() != tabIndex) {
            navigationStack.push(tabIndex);
        }
    }

    /** Phương thức để các fragment gọi chuyển trang */
    public void navigateTo(int navItemId) {
        int current = viewPager.getCurrentItem();
        int target = -1;
        if (navItemId == R.id.nav_fees) target = TAB_FEES;
        else if (navItemId == R.id.nav_home) target = TAB_HOME;
        else if (navItemId == R.id.nav_profile) target = TAB_PROFILE;
        else if (navItemId == R.id.nav_history) target = TAB_HISTORY;
        else if (navItemId == R.id.nav_support) target = TAB_SUPPORT;
        else if (navItemId == R.id.nav_info) target = TAB_INFO;

        if (target != -1 && target != current) {
            pushToStack(current);
            viewPager.setCurrentItem(target, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (!navigationStack.isEmpty()) {
            btnBackHeader.performClick();
        } else {
            super.onBackPressed();
        }
    }

    static class MainPagerAdapter extends FragmentStateAdapter {
        MainPagerAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public int getItemCount() { return 6; }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case TAB_FEES:    return new FeeListFragment();
                case TAB_PROFILE: return new ProfileFragment();
                case TAB_HISTORY: return new HistoryFragment();
                case TAB_SUPPORT: return new SupportFragment();
                case TAB_INFO:    return new InfoFragment();
                default:          return new HomeFragment();
            }
        }
    }
}
