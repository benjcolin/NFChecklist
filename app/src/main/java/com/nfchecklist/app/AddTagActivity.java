package com.nfchecklist.app;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AddTagActivity extends AppCompatActivity {
    public final static int REQUEST_ADD_TAG = 2;
    public DBHelper dbHelper;
    public SimpleCursorAdapter cursorAdapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        dbHelper = new DBHelper(this);

        final Cursor cursor = dbHelper.getAllTags();
        String[] columns = new String[]{
                DBHelper.TAG_COLUMN_NAME,
                DBHelper.TAG_COLUMN_ID
        };
        int[] widgets = new int[]{
                R.id.tagName,
                R.id.tagId
        };

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.tag,
                cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewAddTags);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor itemCursor = (Cursor) AddTagActivity.this.listView.getItemAtPosition(position);
                dbHelper.addTagToChecklist(itemCursor.getInt(itemCursor.getColumnIndex(DBHelper.TAG_COLUMN_ID)));
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
