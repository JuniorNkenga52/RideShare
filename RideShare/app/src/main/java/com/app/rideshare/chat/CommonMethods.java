package com.app.rideshare.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.rideshare.utils.MessageUtils;

import java.util.List;

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

            String Query = "CREATE TABLE IF NOT EXISTS " + tblname + " (ID INTEGER PRIMARY KEY, sender TEXT, receiver TEXT, msg TEXT, who TEXT, type TEXT, time TEXT, msgtype TEXT)";

            mydb.execSQL(Query);

            mydb.close();

        } catch (Exception e) {
            MessageUtils.showFailureMessage(mContext, "Error in creating table");
        }
    }

    public void insertIntoTable(String tablename, String s, String r, String m, String w, String datatype, String time, String msgtype) {

        String tblname = "'" + tablename + "'";

        try {

            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

            String sql = "INSERT INTO " + tblname + " VALUES ( null, ?, ?, ?, ?, ?, ?, ?)";

            mydb.execSQL(sql, new String[]{s, r, m, w, datatype, time, msgtype});

            mydb.close();
            Log.i("DB", "INSERTED");
        } catch (Exception e) {
            Log.i("DBERROR", e.toString());
        }
    }

    public void updateMessages(String tablename, List<MessageModel> messageModelList, String who) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        try {
//            for (MessageModel messageModel : messageModelList) {
//
//                /*//values.put("ID", messageModel.getMsgIdl());
//                values.put("sender", Constants.intentKey.jabberPrefix.toLowerCase() + messageModel.getSender());
//                values.put("receiver", messageModel.getReceiver());
//                values.put("msg", messageModel.getMessageText());
//                values.put("who", who);
//                values.put("type", messageModel.getType());
//                values.put("time", messageModel.getTime());*/
//
//            }
            ContentValues values = new ContentValues();
            values.put("msgtype", "true");
            mydb.update(tablename, values, null, null);
            /*mydb.update(tablename, values, "sender" + " = ? ", new String[]{Constants.intentKey.jabberPrefix.toLowerCase() + messageModel.getSender()});*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mydb.close();
        }
    }

    public boolean isTableExists(String tableName) {

        mydb = mContext.openOrCreateDatabase(CommonMethods.DB_NAME, Context.MODE_PRIVATE, null);

        /*Cursor cursor = mydb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);*/
        Cursor cursor = mydb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        /* Cursor cursor = myDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'"+ " AND sender = '" + senderUser+"'" +" AND receiver = '" + toJabberId+"'", null);*/

        if (cursor != null) {

            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }

            cursor.close();
        }

        return false;
    }

    public void resetDatabase() {
        mContext.deleteDatabase(DB_NAME);
    }
}