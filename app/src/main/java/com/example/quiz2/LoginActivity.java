package com.example.quiz2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnLogin, btnRegistro;
    private TextView tvOlvidarPassword;
    private LottieAnimationView lottieLoading;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("MarvelUserPrefs", MODE_PRIVATE);
        
        // Verificar si ya hay un usuario logueado
        checkUserSession();
    }

    private void initializeViews() {
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etContraseña);
        btnLogin = findViewById(R.id.btnIniciarSesion);
        btnRegistro = findViewById(R.id.btnRegistro);
        tvOlvidarPassword = findViewById(R.id.tvOlvidarPassword);
        lottieLoading = findViewById(R.id.loadingAnimation);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> validateAndLogin());
        
        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
        
        tvOlvidarPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void validateAndLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Limpiar errores previos
        etUsuario.setError(null);
        etPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("El usuario es requerido");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("La contraseña es requerida");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        }

        if (isValid) {
            performLogin(usuario, password);
        }
    }

    private void performLogin(String usuario, String password) {
        // Mostrar animación de carga
        lottieLoading.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Simular proceso de login (aquí iría la validación real)
        new android.os.Handler().postDelayed(() -> {
            // Validar credenciales (por ahora usando credenciales predeterminadas o verificando registro)
            if (validateCredentials(usuario, password)) {
                // Guardar sesión
                saveUserSession(usuario);
                
                // Ir a MainActivity
                Intent intent = new Intent(LoginActivity.this, MainMarvelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
            
            lottieLoading.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }, 2000);
    }

    private boolean validateCredentials(String usuario, String password) {
        // Verificar credenciales predeterminadas de admin
        if (usuario.equals("admin") && password.equals("123456")) {
            return true;
        }
        
        // Verificar credenciales de usuarios registrados
        String savedPassword = sharedPreferences.getString("password_" + usuario, null);
        return savedPassword != null && savedPassword.equals(password);
    }

    private void saveUserSession(String usuario) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("currentUser", usuario);
        editor.putLong("loginTime", System.currentTimeMillis());
        editor.apply();
    }

    private void checkUserSession() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String currentUser = sharedPreferences.getString("currentUser", "");
            if (!currentUser.isEmpty()) {
                // Usuario ya logueado, ir directamente a MainActivity
                startActivity(new Intent(LoginActivity.this, MainMarvelActivity.class));
                finish();
            }
        }
    }
} 