package com.example.pmu.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pmu.R;
import com.example.pmu.fragments.AddFilterFragment_;
import com.example.pmu.fragments.BaseFragment;
import com.example.pmu.fragments.HomePageFragment;
import com.example.pmu.fragments.HomePageFragment_;
import com.example.pmu.fragments.LoginFragment_;
import com.example.pmu.fragments.ProfileFragment_;
import com.example.pmu.fragments.SettingsFragment_;
import com.example.pmu.fragments.WelcomeFragment_;
import com.example.pmu.utils.AppService;
import com.example.pmu.utils.LanguageManager;
import com.example.pmu.utils.RequestBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView navBar;
    public HomePageFragment_ homePageFragment;
    public AddFilterFragment_ addFilterFragment;
    public ProfileFragment_ profileFragment;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.main_activity);
        AppService.getInstance().setContext(this);
        LanguageManager.setApplicationLanguage(this);
        new RequestBuilder(this);
        LayoutInflater mInflater = LayoutInflater.from(this);
        RelativeLayout actionBarTitle = (RelativeLayout) mInflater.inflate(R.layout.actionbar_title, null, false);
        titleView = actionBarTitle.findViewById(R.id.MainTitle);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.buttonColor)));
        navBar = (BottomNavigationView) findViewById(R.id.navBar);
        homePageFragment = new HomePageFragment_();
        addFilterFragment = new AddFilterFragment_();
        profileFragment = new ProfileFragment_();
        addFilterFragment.setSendDataLocation(homePageFragment);
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.map:
                        addFragment(homePageFragment);
                        break;
                    case R.id.request:
                        addFragment(addFilterFragment);
                        break;
                    case R.id.profile:
                        addFragment(profileFragment);
                        break;
                }
                return false;
            }
        });
        if (AppService.getInstance().isOpenedFirst()) {
            addFragmentAndClearBackstack(new LoginFragment_());
        } else {
            addFragmentAndClearBackstack(new WelcomeFragment_());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            addFragment(new SettingsFragment_());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment instanceof BaseFragment && ((BaseFragment) currentFragment).isOverrideBackPressed()) {
            ((BaseFragment) currentFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void setActionBarTitle(String string) {
        titleView.setText(string);
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addFragment(Fragment fragment) {
        addFragment(fragment, true);
    }

    public void addFragment(Fragment fragment, boolean animation) {
        hideSoftKeyboard();

        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (animation) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            ft.setCustomAnimations(0, 0, 0, 0);
        }

        ft.replace(R.id.fragment, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    public void addFragmentAndClearBackstack(Fragment fragment) {
        hideSoftKeyboard();

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(0, 0, 0, 0);
        ft.replace(R.id.fragment, fragment, tag);
        ft.commit();
    }

    public void resetApplication() {
        addFragmentAndClearBackstack(new LoginFragment_());
    }

    public void changeMapView(int mapViewType) {
        homePageFragment.changeMapView(mapViewType);
        AppService.getInstance().setSavedMap(String.valueOf(mapViewType));
        popToHomePageFragment();
    }

    public void popToHomePageFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackEntryCount = fm.getBackStackEntryCount();

        for (int i = backStackEntryCount - 1; i >= 0; i--) {
            FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(i);
            String fragmentTag = backEntry.getName();
            Fragment fragment = fm.findFragmentByTag(fragmentTag);

            if (fragment instanceof HomePageFragment_) {
                return;
            } else {
                fm.popBackStack();
            }
        }
    }
}
