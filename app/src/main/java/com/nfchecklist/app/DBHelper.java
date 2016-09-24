package com.nfchecklist.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Benjamin on 22.09.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    //Datenbank
    public static final String DATABASE_NAME = "NFChecklist.db";
    private static final int DATABSE_VERSION = 1;
    //Tabelle checklist
    public static final String CHECKLIST_TABLE_NAME = "checklist";
    public static final String CHECKLIST_COLUMN_ID = "_id";
    public static final String CHECKLIST_COLUMN_NAME = "name";
    //Tabelle asignedtag
    public static final String ASIGNEDTAG_TABLE_NAME = "asignedtag";
    public static final String ASIGNEDTAG_COLUMN_ID = "_id";
    public static final String ASIGNEDTAG_COLUMN_TAG_IDFS = "tag_idfs";
    public static final String ASIGNEDTAG_COLUMN_CHECKLIST_IDFS = "checklist_idfs";
    public static final String ASIGNEDTAG_COLUMN_CHECKED = "checked";
    //Tabelle tag
    public static final String TAG_TABLE_NAME = "tag";
    public static final String TAG_COLUMN_ID = "_id";
    public static final String TAG_COLUMN_NAME = "name";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CHECKLIST_TABLE_NAME + "(" +
                CHECKLIST_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                CHECKLIST_COLUMN_NAME + " TEXT)"
        );
        db.execSQL("CREATE TABLE " + TAG_TABLE_NAME + "(" +
                TAG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TAG_COLUMN_NAME + " TEXT)"
        );
        db.execSQL("CREATE TABLE " + ASIGNEDTAG_TABLE_NAME + "(" +
                ASIGNEDTAG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                ASIGNEDTAG_COLUMN_TAG_IDFS + " INTEGER NOT NULL, " +
                ASIGNEDTAG_COLUMN_CHECKLIST_IDFS + " INTEGER NOT NULL, " +
                ASIGNEDTAG_COLUMN_CHECKED + " BOOLEAN NOT NULL DEFAULT FALSE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CHECKLIST_TABLE_NAME + ", " + ASIGNEDTAG_TABLE_NAME + ", " + TAG_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTag(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_COLUMN_NAME, name);
        db.insert(TAG_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TAG_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllTagsFromChecklist(int checklistId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TAG_TABLE_NAME +
                                    " JOIN " + ASIGNEDTAG_TABLE_NAME + " ON " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID +
                                    " JOIN " + CHECKLIST_TABLE_NAME + " ON " + CHECKLIST_TABLE_NAME + "." + CHECKLIST_COLUMN_ID + " = " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKLIST_IDFS +
                                    " WHERE " + ASIGNEDTAG_COLUMN_CHECKED + " = " + "1"
                                    , null);
        return res;
    }

    public Integer deleteTag(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TAG_TABLE_NAME,
                TAG_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
