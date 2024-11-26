package com.example.healthy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class AddProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, dobEditText, ageEditText, mobileEditText, passwordEditText;
    private ImageView profileImageView;
    private Button saveProfileButton, selectImageButton;
    private DatabaseHelper databaseHelper;
    private byte[] profileImageBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        dobEditText = findViewById(R.id.dobEditText);
        ageEditText = findViewById(R.id.ageEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        profileImageView = findViewById(R.id.profileImageView);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        databaseHelper = new DatabaseHelper(this);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void selectImage() {
        // In a real application, you can use an image picker library.
        // Here, we just set a placeholder image as a demo.
        Bitmap placeholderImage = BitmapFactory.decodeResource(getResources(), R.drawable.noprofile);
        profileImageView.setImageBitmap(placeholderImage);

        // Convert Bitmap to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        placeholderImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        profileImageBytes = outputStream.toByteArray();
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String dob = dobEditText.getText().toString();
        String ageString = ageEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || ageString.isEmpty()) {
            Toast.makeText(this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseHelper.addUser(name, email, dob, age, mobile, password, profileImageBytes);

        if (result > 0) {
            Toast.makeText(this, "Profile added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddProfileActivity.this, HomeActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to add profile. Email might already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
