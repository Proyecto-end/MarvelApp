package com.example.quiz2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quiz2.api.AuthResponse;
import com.example.quiz2.api.AuthService;
import com.example.quiz2.api.LoginRequest;
import com.example.quiz2.api.AuthConfig;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loadingAnimation;
    private SharedPreferences sharedPreferences;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MarvelUserPrefs", MODE_PRIVATE);
        // Si ya está logueado, ir directo a la pantalla principal
        if (sharedPreferences.getBoolean("isLoggedIn", false)
                && !sharedPreferences.getString("currentUser", "").isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainMarvelActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        // Verificar si hay un usuario registrado
        checkRegisteredUser();
    }

    private void initializeViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.btnIniciarSesion);
        registerButton = findViewById(R.id.btnRegistro);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> validateAndLogin());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        // Limpiar errores previos
        emailLayout.setError(null);
        passwordLayout.setError(null);

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        boolean isValid = true;

        // Validar campos vacíos
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("El correo es requerido");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("La contraseña es requerida");
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

        if (isValid) {
            performLogin(email, password);
        }
    }

    private void performLogin(String email, String password) {
        // Mostrar progreso
        setLoadingVisibility(true);
        loginButton.setEnabled(false);

        LoginRequest request = new LoginRequest(email, password);
        AuthConfig.getAuthService().login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoadingVisibility(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        // Guardar token y datos del usuario
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("authToken", authResponse.getToken());
                        editor.putString("currentUser", email);
                        editor.putString("userName", authResponse.getUser().getName());
                        editor.putString("userEmail", authResponse.getUser().getEmail());
                        editor.apply();

                        // Ir a la pantalla principal
                        Intent intent = new Intent(LoginActivity.this, MainMarvelActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, 
                            authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, 
                        "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoadingVisibility(false);
                Toast.makeText(LoginActivity.this, 
                    "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkRegisteredUser() {
        String registeredEmail = getIntent().getStringExtra("registered_email");
        if (registeredEmail != null) {
            emailInput.setText(registeredEmail);
            Toast.makeText(this, "Registro exitoso. Por favor inicie sesión.", Toast.LENGTH_LONG).show();
        }
    }

    private void setLoadingVisibility(boolean visible) {
        if (loadingAnimation == null) return;
        loadingAnimation.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
} 