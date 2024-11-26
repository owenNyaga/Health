package com.example.healthy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout usersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        usersLayout = findViewById(R.id.users_layout); // Assuming you have this ID in your layout

        // Set click listener for the layout
        usersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllUsers();
            }
        });
    }

    private void showAllUsers() {
        // Start the UsersListActivity
        Intent intent = new Intent(AdminDashboardActivity.this, UsersListActivity.class);
        startActivity(intent);
    }
}
