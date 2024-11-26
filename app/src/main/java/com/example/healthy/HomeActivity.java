package com.example.healthy;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView nameTextView;
    private ImageView profileImageView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nameTextView = findViewById(R.id.nameTextView);
        profileImageView = findViewById(R.id.profileImageView);
        databaseHelper = new DatabaseHelper(this);

        String userEmail = getIntent().getStringExtra("email");
        if (userEmail != null) {
            loadUserProfile(userEmail);
        } else {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile(String email) {
        Cursor cursor = databaseHelper.getUserDetails(email);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            byte[] profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_picture"));

            // Set name
            nameTextView.setText(name);

            // Decode and set profile picture
            if (profilePicture != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length);
                profileImageView.setImageBitmap(bitmap);
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        }
    }
}
