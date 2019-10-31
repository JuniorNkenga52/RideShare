package com.app.rideWhiz.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.MessageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CommonMethods {

    private SQLiteDatabase mydb;

    public static String DB_NAME = "chat.db";

    private Context mContext;

    public CommonMethods(Context context) {
        mContext = context;
    }

    // ============================ Chat Database. ============================================

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

    public void updateMessages(String tablename) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        try {
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
        Cursor cursor = mydb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
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

    // ========================== Start Ride CRUD Operations. ================================

    public void createTableRides(String tablename) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String Query = "CREATE TABLE IF NOT EXISTS " + tblname + " (userID TEXT PRIMARY KEY, userData TEXT)";
            mydb.execSQL(Query);
            mydb.close();
        } catch (Exception e) {
            MessageUtils.showFailureMessage(mContext, "Error in creating table");
        }
    }

    public void insertIntoTableRides(String tablename, String userID, String userData) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String sql = "INSERT INTO " + tblname + " VALUES ( ?, ?)";
            mydb.execSQL(sql, new String[]{userID, userData});
            mydb.close();
            Log.i("DB", "INSERTED");
        } catch (Exception e) {
            Log.i("DBERROR", e.toString());
        }
    }


    public ArrayList<User> fetchRides(String tablename,boolean req_type) {
        ArrayList<User> usersList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + tablename;
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                String userData = cursor.getString(1);
                Gson gson = new Gson();
                Type type = new TypeToken<User>() {
                }.getType();
                User user = gson.fromJson(userData, type);
                if(req_type){
                    if (user.getIs_new_request()) {
                        usersList.add(user);
                    }
                }else {
                    usersList.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return usersList;
    }

    public User fetchUserRide(String tablename) {
        ArrayList<User> usersList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + tablename;
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                String userData = cursor.getString(1);
                Gson gson = new Gson();
                Type type = new TypeToken<User>() {
                }.getType();
                User user = gson.fromJson(userData, type);
                usersList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return usersList.get(0);
    }

    public void updateTableRides(String tablename, String userID, String userData) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        try {
            ContentValues values = new ContentValues();
            values.put("userData", userData);
            mydb.update(tablename, values, "userID" + " = ? ", new String[]{userID});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mydb.close();
        }
    }

    public void deleteRideTable(String tablename) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        mydb.execSQL("DROP TABLE IF EXISTS " + tablename);
        mydb.close();
    }

    public void deleteRideRecord(String tablename, String userID) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        mydb.delete(tablename, "userID" + "=?", new String[]{userID});
        mydb.close();
    }

    // ========================== Start Riders CRUD Operations. ==============================

    public void createTableRiders(String tablename) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String Query = "CREATE TABLE IF NOT EXISTS " + tblname + " (ridersID TEXT PRIMARY KEY, userData TEXT)";
            mydb.execSQL(Query);
            mydb.close();
        } catch (Exception e) {
            MessageUtils.showFailureMessage(mContext, "Error in creating table");
        }
    }

    public void insertIntoTableRiders(String tablename, String userID, String userData) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String sql = "INSERT INTO " + tblname + " VALUES ( ?, ?)";
            mydb.execSQL(sql, new String[]{userID, userData});
            mydb.close();
            Log.i("DB", "INSERTED");
        } catch (Exception e) {
            Log.i("DBERROR", e.toString());
        }
    }

    public ArrayList<AcceptRider> fetchRidersList(String tablename) {
        ArrayList<AcceptRider> usersList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + tablename;
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                String userData = cursor.getString(1);
                Gson gson = new Gson();
                Type type = new TypeToken<AcceptRider>() {
                }.getType();
                AcceptRider rider = gson.fromJson(userData, type);
                usersList.add(rider);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return usersList;
    }

    public void updateTableRiders(String tablename, String userID, String userData) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        try {
            ContentValues values = new ContentValues();
            values.put("userData", userData);
            mydb.update(tablename, values, "ridersID" + " = ? ", new String[]{userID});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mydb.close();
        }
    }

    public void deleteRidersTable(String tablename) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        mydb.execSQL("DROP TABLE IF EXISTS " + tablename);
        mydb.close();
    }

    public void deleteRidersRecord(String tablename, String userID) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        mydb.delete(tablename, "ridersID" + "=?", new String[]{userID});
        mydb.close();
    }

    // ========================== Insert PolyLines Json. ==============================

    public void createTablePloy(String tablename) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String Query = "CREATE TABLE IF NOT EXISTS " + tblname + " (polyID INTEGER PRIMARY KEY AUTOINCREMENT, polyData TEXT)";
            mydb.execSQL(Query);
            mydb.close();
        } catch (Exception e) {
            MessageUtils.showFailureMessage(mContext, "Error in creating table");
        }
    }

    public void insertIntoTablePoly(String tablename,String polyData) {
        String tblname = "'" + tablename + "'";
        try {
            mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            String sql = "INSERT INTO " + tblname + " VALUES (null,?)";
            mydb.execSQL(sql, new String[]{polyData});
            mydb.close();
            Log.i("DB", "INSERTED");
        } catch (Exception e) {
            Log.i("DBERROR", e.toString());
        }
    }

    public void updateTablePoly(String tablename, String polyData) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        try {
            ContentValues values = new ContentValues();
            values.put("polyData", polyData);
            mydb.update(tablename, values,  null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mydb.close();
        }
    }

    public void deletePolyTable(String tablename) {
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        mydb.execSQL("DROP TABLE IF EXISTS " + tablename);
        mydb.close();
    }

    public String fetchPolyData(String tablename) {
        String poly = "";
        String selectQuery = "SELECT  * FROM " + tablename;
        mydb = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = mydb.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                poly= cursor.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return poly;
    }
}