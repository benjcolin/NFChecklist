package com.nfchecklist.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class NewTagActivity extends AppCompatActivity {
    public final static String TAG_NAME = "TAG_NAME";
    public final static int REQUEST_WRITE_TAG = 0;
    public final static int REQUEST_NEW_TAG = 1;
    private EditText tagName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tagName = (EditText) findViewById(R.id.name);
    }

    public void writeTag(View view){
        Intent intent = new Intent(this, WriteTagActivity.class);
        intent.putExtra(TAG_NAME, tagName.getText().toString());
        startActivityForResult(intent, REQUEST_WRITE_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WRITE_TAG && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK, new Intent().putExtra(TAG_NAME, tagName.getText().toString()));
            this.finish();
        }
    }
}
