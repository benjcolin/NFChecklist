package com.nfchecklist.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AllTagsActivity extends AppCompatActivity implements AllTagsFragment.OnFragmentInteractionListener, ChecklistFragment.OnFragmentInteractionListener {

    private static final String MENU_ALLTAGS = "alltags";
    private static final String MENU_CHECKLIST = "checklist";
    public static String CURRENT_MENU = MENU_ALLTAGS;
    public static int navItemIndex = 0;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private String[] activityTitles;
    private Handler mHandler;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tags);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        activityTitles = getResources().getStringArray(R.array.nav_item_acitivity_titles);
        mHandler = new Handler();

        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_MENU = MENU_ALLTAGS;
            loadHomeFragment();
        }
    }

    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_MENU) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_MENU);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.alltags:
                        navItemIndex = 0;
                        CURRENT_MENU = MENU_ALLTAGS;
                        break;
                    case R.id.checklist:
                        navItemIndex = 1;
                        CURRENT_MENU = MENU_CHECKLIST;
                        break;
                    default:
                        navItemIndex = 0;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                AllTagsFragment allTagsFragment = new AllTagsFragment();
                return allTagsFragment;
            case 1:
                ChecklistFragment checklistFragment = new ChecklistFragment();
                return checklistFragment;
            default:
                return new AllTagsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    public void newTag(View view) {
        Intent intent = new Intent(this, NewTagActivity.class);
        startActivityForResult(intent, NewTagActivity.REQUEST_NEW_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NewTagActivity.REQUEST_NEW_TAG && resultCode == Activity.RESULT_OK) {
            //dbHelper.insertTag(data.getStringExtra(NewTagActivity.TAG_NAME));
            //final Cursor cursor = dbHelper.getAllTags();
            //cursorAdapter.changeCursor(cursor);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_MENU = MENU_ALLTAGS;
            loadHomeFragment();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.all_tags, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
