package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class TempConverterActivity extends BaseActivity {

    private EditText editTextTemperature;
    private RadioGroup radioGroupUnits;
    private RadioButton radioButtonCelsius;
    private RadioButton radioButtonFahrenheit;
    private Button buttonConvert;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_converter);

        editTextTemperature = findViewById(R.id.edit_text_temperature);
        radioGroupUnits = findViewById(R.id.radio_group_units);
        radioButtonCelsius = findViewById(R.id.radio_button_celsius);
        radioButtonFahrenheit = findViewById(R.id.radio_button_fahrenheit);
        buttonConvert = findViewById(R.id.button_convert);
        textViewResult = findViewById(R.id.text_view_result);

        buttonConvert.setOnClickListener(v -> convertTemperature());
    }

    private void convertTemperature() {
        String tempStr = editTextTemperature.getText().toString();
        if (TextUtils.isEmpty(tempStr)) {
            Toast.makeText(this, "Por favor, insira um valor.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double temperature = Double.parseDouble(tempStr);
            double convertedTemp;
            String resultUnit;

            int selectedId = radioGroupUnits.getCheckedRadioButtonId();

            if (selectedId == R.id.radio_button_celsius) {
                // Convert Celsius to Fahrenheit
                convertedTemp = (temperature * 9 / 5) + 32;
                resultUnit = "°F";
            } else { // Fahrenheit to Celsius
                convertedTemp = (temperature - 32) * 5 / 9;
                resultUnit = "°C";
            }

            DecimalFormat df = new DecimalFormat("#.##");
            textViewResult.setText(String.format("%s %s", df.format(convertedTemp), resultUnit));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido.", Toast.LENGTH_SHORT).show();
        }
    }
}
