package com.example.quiz2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quiz2.api.ApiConfig;
import com.example.quiz2.api.AuthResponse;
import com.example.quiz2.api.AuthService;
import com.example.quiz2.api.RegisterRequest;
import com.example.quiz2.api.AuthConfig;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroActivity extends AppCompatActivity {
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout birthDateLayout;
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText birthDateInput;
    private Button registerButton;
    private ProgressBar progressBar;
    private Calendar selectedDate;
    private SharedPreferences sharedPreferences;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        initializeViews();
        setupDatePicker();
        setupListeners();
        sharedPreferences = getSharedPreferences("MarvelUserPrefs", MODE_PRIVATE);
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://tu-api.com/") // Reemplazar con la URL real de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        authService = retrofit.create(AuthService.class);
    }

    private void initializeViews() {
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        birthDateLayout = findViewById(R.id.birthDateLayout);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        selectedDate = Calendar.getInstance();
    }

    private void setupDatePicker() {
        birthDateInput.setOnClickListener(v -> showDatePicker());
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> validateAndRegister());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                updateBirthDateField();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateBirthDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        birthDateInput.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void validateAndRegister() {
        // Limpiar errores previos
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        birthDateLayout.setError(null);

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String birthDate = birthDateInput.getText().toString().trim();

        boolean isValid = true;

        // Validar campos vacíos
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("El nombre es requerido");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("El correo es requerido");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("La contraseña es requerida");
            isValid = false;
        }

        if (TextUtils.isEmpty(birthDate)) {
            birthDateLayout.setError("La fecha de nacimiento es requerida");
            isValid = false;
        }

        // Validar formato de email
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Ingrese un correo válido");
            isValid = false;
        }

        // Validar longitud de contraseña
        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            passwordLayout.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        }

        // Validar fecha de nacimiento
        if (!TextUtils.isEmpty(birthDate)) {
            Calendar today = Calendar.getInstance();
            Calendar birthDateCal = Calendar.getInstance();
            birthDateCal.setTime(selectedDate.getTime());
            
            int age = today.get(Calendar.YEAR) - birthDateCal.get(Calendar.YEAR);
            if (birthDateCal.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            
            if (age < 18) {
                birthDateLayout.setError("Debes ser mayor de 18 años");
                isValid = false;
            }
        }

        if (isValid) {
            performRegistration(name, email, password, birthDate);
        }
    }

    private void performRegistration(String name, String email, String password, String birthDate) {
        setLoadingVisibility(true);
        registerButton.setEnabled(false);

        Log.d("RegistroActivity", "Iniciando registro para: " + email);
        RegisterRequest request = new RegisterRequest(name, email, password, birthDate);
        AuthConfig.getAuthService().register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoadingVisibility(false);
                registerButton.setEnabled(true);

                Log.d("RegistroActivity", "Respuesta recibida. Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    Log.d("RegistroActivity", "Respuesta exitosa: " + authResponse.isSuccess());
                    
                    if (authResponse.isSuccess()) {
                        Toast.makeText(RegistroActivity.this, 
                            "Registro exitoso. Por favor inicia sesión.", 
                            Toast.LENGTH_LONG).show();
                        // Guardar datos en SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", name);
                        editor.putString("userEmail", email);
                        editor.putString("birthdate", birthDate);
                        editor.apply();
                        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                        intent.putExtra("registered_email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = authResponse.getMessage();
                        Log.e("RegistroActivity", "Error en respuesta: " + errorMessage);
                        
                        if (errorMessage != null && !errorMessage.isEmpty()) {
                            Toast.makeText(RegistroActivity.this, 
                                errorMessage, 
                                Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegistroActivity.this, 
                                "Error en el registro", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("RegistroActivity", "Error response body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("RegistroActivity", "Error al leer errorBody", e);
                    }
                    
                    String errorMessage = "Error en el servidor (Código: " + response.code() + ")";
                    if (!errorBody.isEmpty()) {
                        errorMessage += ": " + errorBody;
                    }
                    Toast.makeText(RegistroActivity.this, 
                        errorMessage, 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoadingVisibility(false);
                registerButton.setEnabled(true);
                
                String errorMessage = "Error de conexión";
                if (t != null) {
                    Log.e("RegistroActivity", "Error de conexión", t);
                    if (t.getMessage() != null) {
                        errorMessage += ": " + t.getMessage();
                    }
                }
                
                Toast.makeText(RegistroActivity.this, 
                    errorMessage, 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingVisibility(boolean isVisible) {
        progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!isVisible);
    }
} 