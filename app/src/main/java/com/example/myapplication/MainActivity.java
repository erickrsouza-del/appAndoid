package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup button listeners
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.quote_generator_button).setOnClickListener(this);
        findViewById(R.id.temp_converter_button).setOnClickListener(this);
        findViewById(R.id.currency_converter_button).setOnClickListener(this);
        findViewById(R.id.cep_finder_button).setOnClickListener(this);
        findViewById(R.id.shopping_list_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.settings_button) {
            showSettingsMenu(v);
        } else if (id == R.id.quote_generator_button) {
            startActivity(new Intent(this, QuoteGeneratorActivity.class));
        } else if (id == R.id.temp_converter_button) {
            startActivity(new Intent(this, TempConverterActivity.class));
        } else if (id == R.id.currency_converter_button) {
            startActivity(new Intent(this, CurrencyConverterActivity.class));
        } else if (id == R.id.cep_finder_button) {
            startActivity(new Intent(this, CepFinderActivity.class));
        } else if (id == R.id.shopping_list_button) {
            startActivity(new Intent(this, ShoppingListActivity.class));
        }
    }

    private void showSettingsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.action_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}