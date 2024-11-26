package com.example.healthy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UsersListActivity extends AppCompatActivity {

    private TextView usersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        usersTextView = findViewById(R.id.usersTextView);

        // Initialize the database helper and get the list of users
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getAllUsers();

        StringBuilder userList = new StringBuilder();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                userList.append("Name: ").append(name).append("\nEmail: ").append(email).append("\n\n");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            userList.append("No users found.");
        }

        // Set the user list to the TextView
        usersTextView.setText(userList.toString());
    }
}
