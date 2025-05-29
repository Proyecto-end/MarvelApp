package com.example.quiz2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quiz2.clases.Usuario;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etConfirmarPassword;
    private EditText etFechaNacimiento;
    private TextView tvYaTieneCuenta;
    private CheckBox cbTerminos;
    private Button btnRegistrarse, btnVolverLogin;
    private LottieAnimationView lottieLoading;
    private SharedPreferences sharedPreferences;
    private Calendar fechaNacimiento;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        initializeViews();
        setupClickListeners();
        
        sharedPreferences = getSharedPreferences("MarvelUserPrefs", MODE_PRIVATE);
        fechaNacimiento = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void initializeViews() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etContraseña);
        etConfirmarPassword = findViewById(R.id.etConfirmarContraseña);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        tvYaTieneCuenta = findViewById(R.id.btnVolverLogin);
        cbTerminos = findViewById(R.id.cbTerminos);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);
        lottieLoading = findViewById(R.id.loadingAnimation);
    }

    private void setupClickListeners() {
        btnRegistrarse.setOnClickListener(v -> validateAndRegister());
        
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());
        
        btnVolverLogin.setOnClickListener(v -> {
            finish(); // Volver al LoginActivity
        });
    }

    private void showDatePicker() {
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -13); // Mínimo 13 años

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                fechaNacimiento.set(year, month, dayOfMonth);
                etFechaNacimiento.setText(dateFormat.format(fechaNacimiento.getTime()));
            },
            fechaNacimiento.get(Calendar.YEAR),
            fechaNacimiento.get(Calendar.MONTH),
            fechaNacimiento.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void validateAndRegister() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmarPassword = etConfirmarPassword.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString();

        // Limpiar errores previos
        etNombre.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmarPassword.setError(null);

        boolean isValid = true;

        // Validar nombre
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es requerido");
            isValid = false;
        } else if (nombre.length() < 2) {
            etNombre.setError("El nombre debe tener al menos 2 caracteres");
            isValid = false;
        }

        // Validar email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El correo electrónico es requerido");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingrese un correo electrónico válido");
            isValid = false;
        } else if (emailExists(email)) {
            etEmail.setError("Este correo ya está registrado");
            isValid = false;
        }

        // Validar contraseña
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("La contraseña es requerida");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            etPassword.setError("La contraseña debe contener al menos una letra y un número");
            isValid = false;
        }

        // Validar confirmación de contraseña
        if (TextUtils.isEmpty(confirmarPassword)) {
            etConfirmarPassword.setError("Debe confirmar la contraseña");
            isValid = false;
        } else if (!password.equals(confirmarPassword)) {
            etConfirmarPassword.setError("Las contraseñas no coinciden");
            isValid = false;
        }

        // Validar fecha de nacimiento
        if (TextUtils.isEmpty(fechaNac) || fechaNac.equals("DD/MM/AAAA")) {
            Toast.makeText(this, "Debe seleccionar su fecha de nacimiento", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Validar términos y condiciones
        if (!cbTerminos.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            performRegistration(nombre, email, password, fechaNac);
        }
    }

    private boolean emailExists(String email) {
        return sharedPreferences.contains("user_" + email);
    }

    private boolean isPasswordStrong(String password) {
        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        return hasLetter && hasNumber;
    }

    private void performRegistration(String nombre, String email, String password, String fechaNac) {
        // Mostrar animación de carga
        lottieLoading.setVisibility(View.VISIBLE);
        btnRegistrarse.setEnabled(false);

        // Simular proceso de registro
        new android.os.Handler().postDelayed(() -> {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setCorreo(email);
            usuario.setPassword(password);
            usuario.setFechaNacimiento(fechaNac);
            usuario.setId(generateUserId());

            // Guardar usuario en SharedPreferences
            saveUser(usuario);

            Toast.makeText(this, "¡Registro exitoso! Bienvenido a Marvel Studios", Toast.LENGTH_LONG).show();

            // Ir directamente al login
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            intent.putExtra("registered_email", email);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

            lottieLoading.setVisibility(View.GONE);
            btnRegistrarse.setEnabled(true);
        }, 2500);
    }

    private void saveUser(Usuario usuario) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Guardar datos del usuario
        editor.putString("user_" + usuario.getCorreo(), usuario.getNombre());
        editor.putString("password_" + usuario.getCorreo(), usuario.getPassword());
        editor.putString("name_" + usuario.getCorreo(), usuario.getNombre());
        editor.putString("birthdate_" + usuario.getCorreo(), usuario.getFechaNacimiento());
        editor.putString("id_" + usuario.getCorreo(), String.valueOf(usuario.getId()));
        editor.putLong("registration_date_" + usuario.getCorreo(), System.currentTimeMillis());
        
        // Estadísticas iniciales
        editor.putInt("comics_favoritos_" + usuario.getCorreo(), 0);
        editor.putInt("heroes_seguidos_" + usuario.getCorreo(), 0);
        editor.putInt("solicitudes_realizadas_" + usuario.getCorreo(), 0);
        
        editor.apply();
    }

    private int generateUserId() {
        int lastId = sharedPreferences.getInt("last_user_id", 1000);
        int newId = lastId + 1;
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_user_id", newId);
        editor.apply();
        
        return newId;
    }
} 