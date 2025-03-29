package com.example.dropping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DropPing.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACTS = "Contacts";
    private static final String TABLE_GEOFENCES = "Geofences";
    private static final String TABLE_OPTOUTS = "OptOuts";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "phone_number TEXT, " +
                "assigned_locations TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_GEOFENCES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "radius REAL, " +
                "contact_ids TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_OPTOUTS + " (" +
                "phone_number TEXT, " +
                "location_id INTEGER, " +
                "status TEXT, " +
                "PRIMARY KEY (phone_number, location_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOFENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTOUTS);
        onCreate(db);
    }
    public long addContact(String name, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone_number", phoneNumber);
        values.put("assigned_locations", "");
        long id = db.insert(TABLE_CONTACTS, null, values);
        db.close();
        return id;
    }
    public long addGeofence(String name, double latitude, double longitude, float radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("radius", radius);
        values.put("contact_ids", "");
        long id = db.insert(TABLE_GEOFENCES, null, values);
        db.close();
        return id;
    }
    public void setOptStatus(String phoneNumber, int locationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        values.put("location_id", locationId);
        values.put("status", status);
        db.insertWithOnConflict(TABLE_OPTOUTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    public boolean isOptedOut(String phoneNumber, int locationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OPTOUTS, new String[]{"status"},
                "phone_number = ? AND location_id = ?",
                new String[]{phoneNumber, String.valueOf(locationId)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(0);
            cursor.close();
            return "opted_out".equals(status);
        }
        cursor.close();
        return false;
    }
    public String getOptStatus(String phoneNumber, int locationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OPTOUTS, new String[]{"status"},
                "phone_number = ? AND location_id = ?",
                new String[]{phoneNumber, String.valueOf(locationId)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(0);
            cursor.close();
            return status;
        }
        cursor.close();
        return "not_set";
    }
    public void syncContacts(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("phone_number", phoneNumber);
                values.put("assigned_locations", "");
                db.insertWithOnConflict(TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
            cursor.close();
        }
        db.close();
    }
    public List<String[]> getAllContacts() {
        List<String[]> contactsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, phone_number FROM " + TABLE_CONTACTS, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                contactsList.add(new String[]{name, phone});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactsList;
    }
    public List<String[]> getAllGeofences() {
        List<String[]> geofencesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, latitude, longitude FROM " + TABLE_GEOFENCES, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String lat = cursor.getString(1);
                String lon = cursor.getString(2);
                geofencesList.add(new String[]{name, lat, lon});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return geofencesList;
    }
}