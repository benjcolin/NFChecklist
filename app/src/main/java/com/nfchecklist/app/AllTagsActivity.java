package com.nfchecklist.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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
import com.nfchecklist.app.ChecklistFragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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
    private DBHelper dbHelper;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private ChecklistFragment checklistFragment = new ChecklistFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tags);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);

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

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
                checklistFragment = new ChecklistFragment();
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
        Intent intent;
        switch (CURRENT_MENU) {
            case MENU_ALLTAGS:
                intent = new Intent(this, NewTagActivity.class);
                startActivityForResult(intent, NewTagActivity.REQUEST_NEW_TAG);
                break;
            case MENU_CHECKLIST:
                intent = new Intent(this, AddTagActivity.class);
                startActivityForResult(intent, AddTagActivity.REQUEST_ADD_TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NewTagActivity.REQUEST_NEW_TAG && resultCode == Activity.RESULT_OK) {
            dbHelper.insertTag(data.getStringExtra(NewTagActivity.TAG_NAME));
            final Cursor cursor = dbHelper.getAllTags();
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
        if (navItemIndex == 1) {
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
        if (id == R.id.action_clearAll) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void databaseManager(MenuItem menuItem) {
        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) && CURRENT_MENU == MENU_CHECKLIST) {
            Tag mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
            if (mytag == null) {
                //Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_SHORT ).show();
            } else {
                //Toast.makeText(this, mytag.toString(), Toast.LENGTH_SHORT).show();
                Ndef ndef = Ndef.get(mytag);
                NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                NdefRecord[] records = ndefMessage.getRecords();
                for(NdefRecord r : records){
                    if(r.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)){
                        try {
                            String text = readText(r);
                            //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                            dbHelper.setTagChecked(text);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("NFCHECKLIST", "Unsupported Encoding", e);
                        }
                    }
                }
                //Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_SHORT ).show();
            }
        }else {
            super.onNewIntent(intent);
        }
    }



    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    public void clearAll(MenuItem menuItem) {
        dbHelper.clearAll();
        checklistFragment.refreshList();

    }
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null); //ended up setting mFilters and mTechLists to null
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }
}
