package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CepFinderActivity extends BaseActivity {

    private EditText editTextCep;
    private TextView textViewResult;
    private CepApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cep_finder);

        editTextCep = findViewById(R.id.edit_text_cep);
        Button buttonFindCep = findViewById(R.id.button_find_cep);
        textViewResult = findViewById(R.id.text_view_cep_result);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(CepApiService.class);

        // Set manual search button listener
        buttonFindCep.setOnClickListener(v -> findCep());

        // Add automatic search listener
        editTextCep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 8) {
                    findCep();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void findCep() {
        String cep = editTextCep.getText().toString().trim();
        if (cep.length() != 8) {
            Toast.makeText(this, "Por favor, digite um CEP válido com 8 dígitos.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAddress(cep).enqueue(new Callback<CepResponse>() {
            @Override
            public void onResponse(@NonNull Call<CepResponse> call, @NonNull Response<CepResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CepResponse cepResponse = response.body();
                    if (cepResponse.isErro()) {
                        textViewResult.setText("CEP não encontrado.");
                        setResultStyle(false); // Set error style
                    } else {
                        String result = "Logradouro: " + cepResponse.getLogradouro() + "\n"
                                + "Bairro: " + cepResponse.getBairro() + "\n"
                                + "Cidade: " + cepResponse.getLocalidade() + "\n"
                                + "UF: " + cepResponse.getUf();
                        textViewResult.setText(result);
                        setResultStyle(true); // Set success style
                    }
                } else {
                    textViewResult.setText("Falha ao buscar o CEP.");
                    setResultStyle(false); // Set error style
                }
            }

            @Override
            public void onFailure(@NonNull Call<CepResponse> call, @NonNull Throwable t) {
                textViewResult.setText("Erro de rede: " + t.getMessage());
                setResultStyle(false); // Set error style
            }
        });
    }

    private void setResultStyle(boolean isSuccess) {
        if (isSuccess) {
            textViewResult.setBackgroundColor(Color.parseColor("#212121")); // Dark Gray
            textViewResult.setTextColor(Color.WHITE);
        } else {
            textViewResult.setBackgroundColor(Color.parseColor("#f0f0f0")); // Light Gray
            textViewResult.setTextColor(Color.BLACK);
        }
    }
}
