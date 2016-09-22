package com.nfchecklist.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WriteTagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);
        TextView tagName = (TextView) findViewById(R.id.tagName);
        Intent intent = getIntent();
        tagName.setText(intent.getStringExtra(NewTagActivity.EXTRA_MESSAGE));
    }
}
