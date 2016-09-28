package com.nfchecklist.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Benjamin on 22.09.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    //Datenbank
    public static final String DATABASE_NAME = "NFChecklist.db";
    private static final int DATABASE_VERSION = 2;
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
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        //db.execSQL("INSERT INTO TABLE " + CHECKLIST_TABLE_NAME + " (" + CHECKLIST_COLUMN_ID + ", " + CHECKLIST_COLUMN_NAME + ") VALUE ('1','checklist1')");
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHECKLIST_COLUMN_ID, 1);
        contentValues.put(CHECKLIST_COLUMN_NAME, "checklist1");
        db.insert(CHECKLIST_TABLE_NAME, null, contentValues);
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

    public boolean addTagToChecklist(int tagId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ASIGNEDTAG_COLUMN_TAG_IDFS, tagId);
        contentValues.put(ASIGNEDTAG_COLUMN_CHECKLIST_IDFS, 1);
        db.insert(ASIGNEDTAG_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TAG_TABLE_NAME, null);
        return res;
    }

    //TODO: Damit keine Tags doppelt eingefügt werden
    public Cursor getAllTagsToAdd() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TAG_TABLE_NAME + " WHERE " + TAG_COLUMN_ID + " NOT IN (SELECT " + ASIGNEDTAG_COLUMN_TAG_IDFS + " FROM " + ASIGNEDTAG_TABLE_NAME + " )", null);
        return res;
    }

    public Cursor getAllTagsFromChecklist(int checklistId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("NFCHECKLIST", "SELECT " + TAG_TABLE_NAME + "." + TAG_COLUMN_NAME + ", " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID + ", " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKED + " FROM " + TAG_TABLE_NAME +
                " JOIN " + ASIGNEDTAG_TABLE_NAME + " ON " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID +
                " JOIN " + CHECKLIST_TABLE_NAME + " ON " + CHECKLIST_TABLE_NAME + "." + CHECKLIST_COLUMN_ID + " = " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKLIST_IDFS);
        Cursor res = db.rawQuery("SELECT " + TAG_TABLE_NAME + "." + TAG_COLUMN_NAME + ", " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID + ", " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKED + " FROM " + TAG_TABLE_NAME +
                        " JOIN " + ASIGNEDTAG_TABLE_NAME + " ON " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID +
                        " JOIN " + CHECKLIST_TABLE_NAME + " ON " + CHECKLIST_TABLE_NAME + "." + CHECKLIST_COLUMN_ID + " = " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKLIST_IDFS
                        + " ORDER BY " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKED + " ASC"
                , null);
        return res;
    }

    public Integer deleteTag(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ASIGNEDTAG_TABLE_NAME,ASIGNEDTAG_COLUMN_TAG_IDFS + "= ? ", new String[]{Integer.toString(id)});
        return db.delete(TAG_TABLE_NAME,
                TAG_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteTagFromChecklist(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ASIGNEDTAG_TABLE_NAME,
                ASIGNEDTAG_COLUMN_TAG_IDFS + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public void setTagChecked(String tagName) {
        SQLiteDatabase db = getWritableDatabase();
        Log.d("NFCHECKLIST", "SELECT " + TAG_COLUMN_ID + " FROM " + TAG_TABLE_NAME + " WHERE " + TAG_COLUMN_NAME + " = '" + tagName + "'");
        Cursor c = db.rawQuery("SELECT " + TAG_COLUMN_ID + " FROM " + TAG_TABLE_NAME + " WHERE " + TAG_COLUMN_NAME + " = '" + tagName + "'", null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            String tagId = c.getString(c.getColumnIndex(TAG_COLUMN_ID));
            db.execSQL("UPDATE " + ASIGNEDTAG_TABLE_NAME + " SET " + ASIGNEDTAG_COLUMN_CHECKED + "='TRUE' WHERE " + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + tagId);
            Log.d("NFCHECKLIST", "UPDATE " + ASIGNEDTAG_TABLE_NAME + " SET " + ASIGNEDTAG_COLUMN_CHECKED + "='TRUE' WHERE " + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + tagId);
        }
    }

    public void clearAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+ASIGNEDTAG_TABLE_NAME+" SET "+ASIGNEDTAG_COLUMN_CHECKED+" = 'FALSE' ");
    }

    public boolean checkName(String tagName){
        SQLiteDatabase db = getWritableDatabase();
        //Cursor c = db.rawQuery("SELECT * FROM " + TAG_TABLE_NAME + " JOIN " + ASIGNEDTAG_TABLE_NAME + " ON " + ASIGNEDTAG_COLUMN_TAG_IDFS + " = " + TAG_TABLE_NAME + "." + TAG_COLUMN_ID + " WHERE " + TAG_TABLE_NAME + "." + TAG_COLUMN_NAME + " = '" + tagName + "' AND " + ASIGNEDTAG_TABLE_NAME + "." + ASIGNEDTAG_COLUMN_CHECKED + " = 'FALSE", null);
        Cursor c = db.rawQuery("SELECT " + TAG_COLUMN_ID + " FROM " + TAG_TABLE_NAME + " WHERE " + TAG_COLUMN_NAME + " = '" + tagName + "'", null);
        c.moveToFirst();
        if (c.getCount() != 0) {
            return false;
        }else {
            return true;
        }
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }



    }
}
