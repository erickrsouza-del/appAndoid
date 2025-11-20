package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

public class TempConverterActivity extends BaseActivity {

    private enum Unit {
        CELSIUS, FAHRENHEIT, KELVIN
    }

    private EditText editTextFromValue;
    private TextView textViewToValue;
    private Spinner spinnerFromUnit, spinnerToUnit;
    private ImageButton buttonSwapUnits;

    private final DecimalFormat df = new DecimalFormat("#.##");
    private boolean isUserAction = true; // Flag to prevent listener loops

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_converter);

        editTextFromValue = findViewById(R.id.edit_text_from_value);
        textViewToValue = findViewById(R.id.text_view_to_value);
        spinnerFromUnit = findViewById(R.id.spinner_from_unit);
        spinnerToUnit = findViewById(R.id.spinner_to_unit);
        buttonSwapUnits = findViewById(R.id.button_swap_units);

        setupSpinners();

        buttonSwapUnits.setOnClickListener(v -> swapUnits());

        editTextFromValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUserAction) convertTemperature();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSpinners() {
        ArrayAdapter<Unit> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Unit.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFromUnit.setAdapter(adapter);
        spinnerToUnit.setAdapter(adapter);

        spinnerFromUnit.setSelection(Unit.CELSIUS.ordinal());
        spinnerToUnit.setSelection(Unit.FAHRENHEIT.ordinal());

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isUserAction) convertTemperature();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFromUnit.setOnItemSelectedListener(listener);
        spinnerToUnit.setOnItemSelectedListener(listener);
    }

    private void swapUnits() {
        isUserAction = false; // Disable listeners to prevent loops
        int fromPos = spinnerFromUnit.getSelectedItemPosition();
        int toPos = spinnerToUnit.getSelectedItemPosition();

        spinnerFromUnit.setSelection(toPos);
        spinnerToUnit.setSelection(fromPos);

        CharSequence tempValue = editTextFromValue.getText();
        editTextFromValue.setText(textViewToValue.getText());
        
        isUserAction = true;
        // The TextWatcher on editTextFromValue will trigger the conversion
    }

    private void convertTemperature() {
        String fromValueStr = editTextFromValue.getText().toString();

        if (TextUtils.isEmpty(fromValueStr) || fromValueStr.equals("-") || fromValueStr.equals(".")) {
            textViewToValue.setText("");
            return;
        }

        try {
            double fromValue = Double.parseDouble(fromValueStr);
            Unit fromUnit = (Unit) spinnerFromUnit.getSelectedItem();
            Unit toUnit = (Unit) spinnerToUnit.getSelectedItem();

            // Step 1: Convert input value to a base unit (Celsius)
            double valueInCelsius;
            switch (fromUnit) {
                case FAHRENHEIT:
                    valueInCelsius = (fromValue - 32) * 5 / 9;
                    break;
                case KELVIN:
                    valueInCelsius = fromValue - 273.15;
                    break;
                case CELSIUS:
                default:
                    valueInCelsius = fromValue;
                    break;
            }

            // Step 2: Convert from Celsius to the target unit
            double toValue;
            switch (toUnit) {
                case FAHRENHEIT:
                    toValue = (valueInCelsius * 9 / 5) + 32;
                    break;
                case KELVIN:
                    toValue = valueInCelsius + 273.15;
                    break;
                case CELSIUS:
                default:
                    toValue = valueInCelsius;
                    break;
            }

            textViewToValue.setText(df.format(toValue));

        } catch (NumberFormatException e) {
            textViewToValue.setText("Err");
        }
    }
}
