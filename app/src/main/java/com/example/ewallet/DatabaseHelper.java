package com.example.ewallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EWallet.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_GENDER = "gender";
    private static final String COL_ADDRESS = "address";
    private static final String COL_BALANCE = "balance";
    private static final String COL_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_FULL_NAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_BALANCE + " REAL DEFAULT 0, " +
                COL_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTable);

        insertDefaultUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertDefaultUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, "+84912345678");
        values.put(COL_PASSWORD, "123456");
        values.put(COL_FULL_NAME, "Anh Phúc");
        values.put(COL_GENDER, "Nam");
        values.put(COL_ADDRESS, "Hà Nội, Việt Nam");
        values.put(COL_BALANCE, 30000.00);
        db.insert(TABLE_USERS, null, values);
    }

    public boolean insertUser(String username, String password) {
        return insertUser(username, password, null, null, null);
    }

    public boolean insertUser(String username, String password, String fullName, String gender, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_GENDER, gender);
        values.put(COL_ADDRESS, address);
        values.put(COL_BALANCE, 0.0);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS)));
            user.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BALANCE)));
        }
        cursor.close();
        return user;
    }

    public boolean updateBalance(String username, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BALANCE, newBalance);
        int rows = db.update(TABLE_USERS, values, COL_USERNAME + "=?", new String[]{username});
        return rows > 0;
    }

    public double getBalance(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        double balance = 0;
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        return balance;
    }

    public static class User {
        private int id;
        private String username;
        private String fullName;
        private String gender;
        private String address;
        private double balance;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
    }
}