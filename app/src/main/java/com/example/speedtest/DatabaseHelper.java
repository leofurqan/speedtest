package com.example.speedtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance;

    //Database Information
    private static final String DATABASE_NAME = "ericsson_speedtest";
    private static final int DATABASE_VERSION = 1;

    //Tables Information
    private static final String TABLE_DNS = "dns";
    private static final String TABLE_HISTORY = "history";

    //Fields Information
    //DNS
    private static final String DNS_ID = "id";
    private static final String DNS_IP = "ip";

    //History
    private static final String HISTORY_ID = "id";
    private static final String HISTORY_IP = "ip";
    private static final String HISTORY_PING = "ping";
    private static final String HISTORY_JITTER = "jitter";
    private static final String HISTORY_LOSS = "loss";
    private static final String HISTORY_DOWNLOAD = "download";
    private static final String HISTORY_UPLOAD = "upload";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }

        return mInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Queries
        String CREATE_DNS_TABLE = "CREATE TABLE " + TABLE_DNS + "(" + DNS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + DNS_IP + " TEXT" + ")";
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "(" + HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + HISTORY_IP + " TEXT," + HISTORY_PING + " TEXT," + HISTORY_JITTER + " TEXT," + HISTORY_LOSS + " TEXT," + HISTORY_DOWNLOAD + " TEXT," + HISTORY_UPLOAD + " TEXT" + ")";

        db.execSQL(CREATE_DNS_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DNS);
            onCreate(db);
        }
    }

    // Settings
    public void addDNS(DNSData dns) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DNS_IP, dns.getIp());

            db.insert(TABLE_DNS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("e_insert_dns", e.toString());
        } finally {
            db.endTransaction();
        }
    }

    public DNSData getDNSByID(int id) {
        DNSData dnsData = new DNSData();
        SQLiteDatabase db = getReadableDatabase();
        String selection = DNS_ID + " = ?";
        String[] selectionArgs = {id + ""};
        Cursor cursor = db.query(TABLE_DNS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            dnsData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DNS_ID)));
            dnsData.setIp(cursor.getString(cursor.getColumnIndexOrThrow(DNS_IP)));

            cursor.close();
        }

        return dnsData;
    }

    public ArrayList<DNSData> getAllDNS() {
        ArrayList<DNSData> dnsData = new ArrayList<>();
        String MATCHES_QUERY = "SELECT * FROM " + TABLE_DNS + " ORDER BY " + DNS_ID + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(MATCHES_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    DNSData dns = new DNSData();
                    dns.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DNS_ID)));
                    dns.setIp(cursor.getString(cursor.getColumnIndexOrThrow(DNS_IP)));

                    dnsData.add(dns);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("error_read_dns", e.toString());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return dnsData;
    }

    //History
    public void addHistory(HistoryData history) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(HISTORY_IP, history.getIp());
            values.put(HISTORY_PING, history.getPing());
            values.put(HISTORY_JITTER, history.getJitter());
            values.put(HISTORY_LOSS, history.getLoss());
            values.put(HISTORY_DOWNLOAD, history.getDownload());
            values.put(HISTORY_UPLOAD, history.getUpload());

            db.insert(TABLE_HISTORY, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("e_insert_history", e.toString());
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<HistoryData> getAllHistory() {
        ArrayList<HistoryData> historyData = new ArrayList<>();
        String HISTORY_QUERY = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + HISTORY_ID + " DESC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(HISTORY_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    HistoryData history = new HistoryData();
                    history.setId(cursor.getInt(cursor.getColumnIndexOrThrow(HISTORY_ID)));
                    history.setIp(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_IP)));
                    history.setPing(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_PING)));
                    history.setJitter(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_JITTER)));
                    history.setLoss(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_LOSS)));
                    history.setDownload(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_DOWNLOAD)));
                    history.setUpload(cursor.getString(cursor.getColumnIndexOrThrow(HISTORY_UPLOAD)));

                    historyData.add(history);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("error_read_history", e.toString());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return historyData;
    }
}