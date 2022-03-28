package com.example.courseproject_new.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.courseproject_new.model.Event;

public class DBHelper extends SQLiteOpenHelper {
    private static final int SCHEMA = 1;
    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "valentine_db";
    private static final String TABLE_NAME = "event_table";
    private static DBHelper instance = null;


    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, SCHEMA);
    }

    public static DBHelper getInstance(Context context) {
        if(instance == null) instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( "
                + "EVENT_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "TITLE TEXT NOT NULL, "
                + "DESCRIPTION TEXT NOT NULL, "
                + "PRICE REAL NOT NULL, "
                + "DATE TEXT NOT NULL, "
                + "TIME TEXT NOT NULL, "
                + "ENDDATE TEXT NOT NULL, "
                + "ENDTIME TEXT NOT NULL, "
                + "PICTURE BLOB NOT NULL, "
                + "LOCATION  TEXT NOT NULL );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public static long addEvent(SQLiteDatabase db, Event event){
        ContentValues  contentValues = new ContentValues();
        contentValues.put("TITLE", event.getTitle());
        contentValues.put("DESCRIPTION", event.getDescription());
        contentValues.put("PRICE", event.getPrice());
        contentValues.put("DATE", event.getDate());
        contentValues.put("TIME", event.getTime());
        contentValues.put("ENDDATE", event.getEndDate());
        contentValues.put("ENDTIME", event.getEndTime());
        contentValues.put("PICTURE", event.getPicture());
        contentValues.put("LOCATION", event.getAdress());

       return db.insertOrThrow(TABLE_NAME, null, contentValues);
    }

    public static long deleteEvent(SQLiteDatabase db, int event) {

        return db.delete(TABLE_NAME, "EVENT_ID = ?", new String[] {String.valueOf(event)});
    }
    public static Cursor getEvents(SQLiteDatabase db) {
        return db.rawQuery("select * from " + TABLE_NAME + ";", null);
    }
    public static Cursor getEventPictureByID(SQLiteDatabase db, int event_id) {
        return db.rawQuery("select PICTURE from " + TABLE_NAME + " where EVENT_ID = " + event_id + ";", null);
    }

    public static Cursor getEventsSortedByTitle(SQLiteDatabase db){
        db.execSQL("create index if not exists idx_EvetnsTable on " + TABLE_NAME + "("
                + "EVENT_ID)");

        String createView =
                "create temp view if not exists SORTEDBYTITLE_view"
                        + " as select * from " + TABLE_NAME
                        + " order by TITLE desc";
        db.execSQL(createView);
        return db.rawQuery("select * from SORTEDBYTITLE_view", null);
    }
    public static Cursor getEventsSortedByTitleAsc(SQLiteDatabase db){
        db.execSQL("create index if not exists idx_EvetnsTable on " + TABLE_NAME + "("
                + "EVENT_ID)");

        String createView =
                "create temp view if not exists SORTEDBYTITLEASC_view"
                        + " as select * from " + TABLE_NAME
                        + " order by TITLE asc";
        db.execSQL(createView);
        return db.rawQuery("select * from SORTEDBYTITLEASC_view", null);
    }

    public static Cursor getEventsSortedByDateDesc(SQLiteDatabase db){
        db.execSQL("create index if not exists idx_EvetnsTable on " + TABLE_NAME + "("
                + "EVENT_ID)");

        String createView =
                "create temp view if not exists SORTEDBYDATEDESC_view"
                        + " as select * from " + TABLE_NAME
                        + " order by date(" + TABLE_NAME + ".DATE) desc";
        db.execSQL(createView);
        return db.rawQuery("select * from SORTEDBYDATEDESC_view", null);
    }
    public static Cursor getEventsSortedByDateAsc(SQLiteDatabase db){
        db.execSQL("create index if not exists idx_EvetnsTable on " + TABLE_NAME + "("
                + "EVENT_ID)");

        String createView =
                "create temp view if not exists SORTEDBYDATEASC_view"
                        + " as select * from " + TABLE_NAME
                        + " order by date(" + TABLE_NAME + ".DATE) asc";
        db.execSQL(createView);
        return db.rawQuery("select * from SORTEDBYDATEASC_view", null);
    }


    public static long updateEvent(SQLiteDatabase db, Event event, int id) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("EVENT_ID", event.getEvent_id());
        contentValues.put("TITLE", event.getTitle());
        contentValues.put("DESCRIPTION", event.getDescription());
        contentValues.put("PRICE", event.getPrice());
        contentValues.put("DATE", event.getDate());
        contentValues.put("TIME", event.getTime());
        contentValues.put("ENDDATE", event.getEndDate());
        contentValues.put("ENDTIME", event.getEndTime());
        contentValues.put("PICTURE", event.getPicture());
        contentValues.put("LOCATION", event.getAdress());

        return db.update(TABLE_NAME, contentValues, "EVENT_ID = ?", new String[] {String.valueOf(id)});
    }
}
