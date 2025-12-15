package com.example.focusflowperson2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "userDB.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_GENDER = "gender";
    private static final String COL_PHONE = "phone";
    private static final String COL_IMAGE = "image";

    public UserDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_EMAIL + " TEXT UNIQUE, "
                + COL_GENDER + " TEXT, "  // Added space before TEXT
                + COL_PHONE + " TEXT, "
                + COL_IMAGE + " TEXT, "   // Added space before TEXT
                + COL_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert a new user
    public boolean insertUser(String username, String email, String password) {
        String hashedPassword = hashPassword(password);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, hashedPassword);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // success if not -1
    }

    // Check login
    public boolean checkUser(String username, String password) {
        String hashedPassword = hashPassword(password);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, hashedPassword});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // SHA-256 password hashing
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // fallback (not recommended, but safe for app)
        }
    }

    public String getEmailByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        }
        cursor.close();
        return "";
    }


    // Update user data in profile page
    public boolean updateUser(String email, String username, String phone, String gender, String profileImageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("username", username);
        cv.put("phone", phone);
        cv.put("gender", gender);
        cv.put("image", profileImageUri);

        int result = db.update("users", cv, "email = ?", new String[]{email});
        return result > 0;
    }


}

