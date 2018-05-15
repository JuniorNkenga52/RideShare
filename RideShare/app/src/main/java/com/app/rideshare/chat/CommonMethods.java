package com.app.rideshare.chat;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.rideshare.utils.MessageUtils;

public class CommonMethods {

    private SQLiteDatabase mydb;

    public static String DB_NAME = "chat.db";

    private Context mContext;

    public CommonMethods(Context context) {
        mContext = context;
    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        return context.getDatabasePath(dbName).exists();
    }

    public void createTable(String tablename) {

        String tblname = "'" + tablename + "'";

        try {

            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

            String Query = "CREATE TABLE IF NOT EXISTS " + tblname + " (ID INTEGER PRIMARY KEY, sender TEXT, receiver TEXT, msg TEXT, who TEXT, type TEXT, time TEXT)";

            mydb.execSQL(Query);

            mydb.close();

        } catch (Exception e) {
            MessageUtils.showFailureMessage(mContext, "Error in creating table");
        }
    }

    public void insertIntoTable(String tablename, String s, String r, String m, String w, String datatype, String time) {

        String tblname = "'" + tablename + "'";

        try {

            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

            String sql = "INSERT INTO " + tblname + " VALUES ( null, ?, ?, ?, ?, ?, ?)";

            mydb.execSQL(sql, new String[]{s, r, m, w, datatype, time});

            mydb.close();
            Log.i("DB", "INSERTED");
        } catch (Exception e) {
            Log.i("DBERROR", e.toString());
        }
    }

    public void resetDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}