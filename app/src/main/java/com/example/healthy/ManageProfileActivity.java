package com.example.healthy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageProfileActivity extends AppCompatActivity {

    private EditText nameEditText, mobileEditText;
    private ImageView profileImageView;
    private Button updateProfileButton, addNewProfileButton;
    private DatabaseHelper databaseHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        nameEditText = findViewById(R.id.nameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        profileImageView = findViewById(R.id.profileImageView);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        addNewProfileButton = findViewById(R.id.addNewProfileButton);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null) {
            loadUserProfile(userEmail);
        }

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        addNewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageProfileActivity.this, AddProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUserProfile(String email) {
        Cursor cursor = databaseHelper.getUserDetails(email);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow("mobile"));
            byte[] profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));

            nameEditText.setText(name);
            mobileEditText.setText(mobile);

            if (profilePicture != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length);
                profileImageView.setImageBitmap(bitmap);
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();

        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsUpdated = databaseHelper.updateUserProfile(userEmail, name, mobile, null);
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
