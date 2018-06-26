package com.example.spirit.androiddemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.spirit.androiddemo.utils.DataUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initVariable();
        toolbar.setTitle(R.string.tel);
        setSupportActionBar(toolbar);
        replaceFragment(DataUtil.getFragment(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string
                .navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initVariable() {
        fragmentManager = getSupportFragmentManager();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView
                    .getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tel) {
            changePage(R.string.tel, 0);
        } else if (id == R.id.sms) {
            changePage(R.string.sms, 1);
        } else if (id == R.id.soft) {
            changePage(R.string.soft, 2);
        } else if (id == R.id.pic) {
            changePage(R.string.pic, 3);
        } else if (id == R.id.music) {
            changePage(R.string.music, 4);
        } else if (id == R.id.video) {
            changePage(R.string.video, 5);
        } else if (id == R.id.weather) {
            changePage(R.string.weather, 6);
        } else if (id == R.id.capture) {
            changePage(R.string.capture, 7);
        } else if (id == R.id.error) {
            changePage(R.string.errorNote, 8);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePage(int title, int i) {
        toolbar.setTitle(title);
        replaceFragment(DataUtil.getFragment(i));
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
                .replace(R.id.fl_container, fragment).commit();
    }
}
