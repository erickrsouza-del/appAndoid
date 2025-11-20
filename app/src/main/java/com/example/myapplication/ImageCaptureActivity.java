package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ImageCaptureActivity extends BaseActivity {

    private ImageView imageView;
    private LinearLayout actionButtonsLayout;
    private Uri latestTmpUri;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), isSuccess -> {
                if (isSuccess) {
                    imageView.setImageURI(latestTmpUri);
                    actionButtonsLayout.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        imageView = findViewById(R.id.image_view_captured);
        Button takePictureButton = findViewById(R.id.button_take_picture);
        actionButtonsLayout = findViewById(R.id.action_buttons_layout);

        takePictureButton.setOnClickListener(v -> takePicture());
        findViewById(R.id.button_save).setOnClickListener(v -> saveToGallery());
        findViewById(R.id.button_share).setOnClickListener(v -> shareImage());
    }

    private void takePicture() {
        try {
            File tmpFile = File.createTempFile("tmp_image_", ".png", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            latestTmpUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tmpFile);
            takePictureLauncher.launch(latestTmpUri);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToGallery() {
        if (latestTmpUri == null) return;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), latestTmpUri);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "capture_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            }
            Uri externalUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (externalUri != null) {
                try (OutputStream os = getContentResolver().openOutputStream(externalUri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, os);
                }
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage() {
        if (latestTmpUri == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, latestTmpUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}
