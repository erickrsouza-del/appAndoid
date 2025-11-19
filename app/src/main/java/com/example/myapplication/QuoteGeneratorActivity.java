package com.example.myapplication;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteGeneratorActivity extends BaseActivity {

    private static final String TAG = "QuoteGenerator";
    private final String UNSPLASH_API_KEY = "Client-ID GhVwFZtEl0a49-PHMKoL8tELC2y8ZC9Hyiulahp8eDE";

    private TextView quoteTextView;
    private ImageView quoteImageView;
    private UnsplashApiService unsplashApiService;
    private List<Quote> motivationalQuotes, funnyQuotes;
    private Random random = new Random();

    private enum Category { MOTIVATIONAL, FUNNY }
    private Category selectedCategory = null;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    saveImageToGallery();
                } else {
                    Toast.makeText(this, R.string.permission_denied_to_save, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_generator);

        quoteTextView = findViewById(R.id.quote_text_view);
        quoteImageView = findViewById(R.id.quote_image_view);
        MaterialButton motivationalButton = findViewById(R.id.motivational_button);
        MaterialButton funnyButton = findViewById(R.id.funny_button);

        findViewById(R.id.generate_quote_button).setOnClickListener(v -> generateRandomQuote());
        findViewById(R.id.copy_quote_button).setOnClickListener(v -> copyQuoteToClipboard());
        findViewById(R.id.save_image_button).setOnClickListener(v -> checkPermissionAndSaveImage());

        initializeQuotes();
        setupRetrofit();
        
        quoteTextView.setText(R.string.quote_initial_instruction);

        motivationalButton.setOnClickListener(v -> selectCategory(Category.MOTIVATIONAL, motivationalButton, funnyButton));
        funnyButton.setOnClickListener(v -> selectCategory(Category.FUNNY, funnyButton, motivationalButton));
    }

    private void setupRetrofit() {
        Retrofit unsplashRetrofit = new Retrofit.Builder().baseUrl("https://api.unsplash.com/").addConverterFactory(GsonConverterFactory.create()).build();
        unsplashApiService = unsplashRetrofit.create(UnsplashApiService.class);
    }

    private void initializeQuotes() {
        motivationalQuotes = new ArrayList<>();
        motivationalQuotes.add(new Quote("A persistência é o caminho do êxito.", "path"));
        motivationalQuotes.add(new Quote("O sucesso nasce do querer, da determinação e persistência.", "mountain top"));
        motivationalQuotes.add(new Quote("Acredite em si próprio e chegará um dia em que os outros não terão outra escolha senão acreditar com você.", "believe"));
        motivationalQuotes.add(new Quote("A vida é 10% o que acontece a você e 90% como você reage a isso.", "reflection"));
        motivationalQuotes.add(new Quote("O único lugar onde o sucesso vem antes do trabalho é no dicionário.", "book"));

        funnyQuotes = new ArrayList<>();
        funnyQuotes.add(new Quote("Não sou preguiçoso, estou em modo de economia de energia.", "lazy cat"));
        funnyQuotes.add(new Quote("Minha carteira é igual uma cebola, quando abro eu choro.", "empty wallet"));
        funnyQuotes.add(new Quote("A realidade é uma ilusão que ocorre devido à falta de café.", "coffee"));
    }

    private void selectCategory(Category category, Button selected, Button unselected) {
        selectedCategory = category;
        selected.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
        selected.setTextColor(Color.WHITE);
        unselected.setBackgroundColor(Color.TRANSPARENT);
        unselected.setTextColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
    }

    private void generateRandomQuote() {
        if (selectedCategory == null) {
            Toast.makeText(this, R.string.select_category_first, Toast.LENGTH_SHORT).show();
            return;
        }
        List<Quote> list = (selectedCategory == Category.MOTIVATIONAL) ? motivationalQuotes : funnyQuotes;
        Quote quote = list.get(random.nextInt(list.size()));
        quoteTextView.setText(quote.getText());
        fetchImageForQuote(quote.getImageKeyword());
    }

    private void fetchImageForQuote(String query) {
        unsplashApiService.searchPhotos(query, 1, UNSPLASH_API_KEY).enqueue(new Callback<UnsplashResponse>() {
            @Override
            public void onResponse(@NonNull Call<UnsplashResponse> call, @NonNull Response<UnsplashResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                    String imageUrl = response.body().getResults().get(0).getUrls().getRegular();
                    Glide.with(QuoteGeneratorActivity.this).load(imageUrl).into(quoteImageView);
                } else {
                    Toast.makeText(QuoteGeneratorActivity.this, R.string.image_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<UnsplashResponse> call, @NonNull Throwable t) {
                Toast.makeText(QuoteGeneratorActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyQuoteToClipboard() {
        String textToCopy = quoteTextView.getText().toString();
        String initialText = getString(R.string.quote_initial_instruction);
        if (textToCopy.isEmpty() || textToCopy.equals(initialText)) {
            Toast.makeText(this, R.string.generate_quote_to_copy, Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("quote", textToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.quote_copied, Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionAndSaveImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToGallery();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void saveImageToGallery() {
        Drawable drawable = quoteImageView.getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            Toast.makeText(this, R.string.no_image_to_save, Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmapToSave = ((BitmapDrawable) drawable).getBitmap();
        
        try {
            final String displayName = "Quote_" + System.currentTimeMillis() + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            }

            Uri collectionUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri itemUri = getContentResolver().insert(collectionUri, values);

            if (itemUri != null) {
                try (OutputStream os = getContentResolver().openOutputStream(itemUri)) {
                    bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 95, os);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(itemUri, values, null, null);
                }
                Toast.makeText(this, R.string.image_saved_to_gallery, Toast.LENGTH_SHORT).show();
            } else {
                throw new Exception("Failed to create MediaStore record.");
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.image_save_failed, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error saving image to gallery", e);
        }
    }
}