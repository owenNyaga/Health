package com.example.healthy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 2;

    // User Table and Columns
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture";

    // Create Users Table Query
    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_AGE + " INTEGER, " +
                    COLUMN_MOBILE + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_PROFILE_PICTURE + " BLOB" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_USERS);
        insertDefaultAdminAccount(db);  // Insert default admin account
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Utility Functions
    private String hashPassword(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertDefaultAdminAccount(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_EMAIL},
                COLUMN_EMAIL + " = ?",
                new String[]{"admin@gmail.com"},
                null, null, null);

        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, "Admin");
            values.put(COLUMN_EMAIL, "admin@gmail.com");
            values.put(COLUMN_PASSWORD, hashPassword("admin")); // Secure hashed password
            db.insert(TABLE_USERS, null, values);
            Log.d("DatabaseHelper", "Default admin account inserted.");
        } else {
            Log.d("DatabaseHelper", "Admin account already exists.");
        }
        cursor.close();
    }

    // User Functions
    public long addUser(String name, String email, String dob, int age, String mobile, String password, byte[] profilePicture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_PASSWORD, hashPassword(password)); // Store the hashed password
        values.put(COLUMN_PROFILE_PICTURE, profilePicture);

        return db.insert(TABLE_USERS, null, values);
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                null,
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null);
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, null, null, null, null, COLUMN_NAME + " ASC");
    }

    public boolean verifyUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, hashPassword(password)}, // Compare hashed password
                null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public int updateUserProfile(String email, String name, String mobile, byte[] profilePicture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_PROFILE_PICTURE, profilePicture);

        return db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
    }

    public int deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_EMAIL + " = ?", new String[]{email});
    }
}
