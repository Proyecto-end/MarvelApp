package com.example.quiz2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ConfiguracionFragment extends Fragment {

    private ImageView ivAvatarUsuario;
    private TextView tvNombreUsuario, tvCorreoUsuario;
    private EditText etNombreEditar, etCorreoEditar, etFechaNacimiento;
    private TextView tvComicsFavoritos, tvHeroesSeguidos, tvSolicitudesRealizadas;
    private TextView tvFechaRegistro, tvUltimoAcceso;
    private Switch switchNotificaciones, switchTemaOscuro, switchSonidos;
    private Button btnEditarPerfil, btnGuardarCambios, btnCancelarEdicion, btnCerrarSesion;
    private View layoutVisualizacion, layoutEdicion, layoutEstadisticas;
    private LottieAnimationView lottieAvatar;

    private String currentUser;
    private SharedPreferences sharedPreferences;
    private boolean isEditMode = false;
    private Calendar fechaNacimiento;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        
        initializeViews(view);
        setupUserInfo();
        setupClickListeners();
        loadUserData();
        loadUserStats();
        loadUserSettings();
        
        return view;
    }

    private void initializeViews(View view) {
        // Avatar y nombre
        ivAvatarUsuario = view.findViewById(R.id.ivAvatar);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = view.findViewById(R.id.tvCorreoUsuario);
        
        // Campos de edición
        etNombreEditar = view.findViewById(R.id.etNombreEditar);
        etCorreoEditar = view.findViewById(R.id.etCorreoEditar);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        
        // Estadísticas
        tvComicsFavoritos = view.findViewById(R.id.tvComicsFavoritos);
        tvHeroesSeguidos = view.findViewById(R.id.tvHeroesSeguidos);
        tvSolicitudesRealizadas = view.findViewById(R.id.tvSolicitudes);
        tvFechaRegistro = view.findViewById(R.id.tvFechaRegistro);
        tvUltimoAcceso = view.findViewById(R.id.tvUltimoAcceso);
        
        // Configuraciones
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones);
        switchTemaOscuro = view.findViewById(R.id.switchTemaOscuro);
        switchSonidos = view.findViewById(R.id.switchSonidos);
        
        // Botones
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);
        btnCancelarEdicion = view.findViewById(R.id.btnCancelarEdicion);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        
        // Layouts
        layoutVisualizacion = view.findViewById(R.id.cardProfile);
        layoutEdicion = view.findViewById(R.id.cardInfo);
        layoutEstadisticas = view.findViewById(R.id.cardStats);
        
        // Inicializar calendario
        fechaNacimiento = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void setupUserInfo() {
        if (getActivity() instanceof MainMarvelActivity) {
            MainMarvelActivity mainActivity = (MainMarvelActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
            sharedPreferences = mainActivity.getMarvelSharedPreferences();
        }
    }

    private void setupClickListeners() {
        btnEditarPerfil.setOnClickListener(v -> enableEditMode());
        btnGuardarCambios.setOnClickListener(v -> saveChanges());
        btnCancelarEdicion.setOnClickListener(v -> cancelEdit());
        btnCerrarSesion.setOnClickListener(v -> showLogoutDialog());
        
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());
        
        // Listeners para switches
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> 
            saveSettingPreference("notificaciones", isChecked));
        
        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> 
            saveSettingPreference("tema_oscuro", isChecked));
        
        switchSonidos.setOnCheckedChangeListener((buttonView, isChecked) -> 
            saveSettingPreference("sonidos", isChecked));
    }

    private void loadUserData() {
        if (currentUser != null && sharedPreferences != null) {
            String nombreUsuario = sharedPreferences.getString("name_" + currentUser, currentUser);
            String correoUsuario = sharedPreferences.getString("user_" + currentUser, currentUser);
            String fechaNac = sharedPreferences.getString("birthdate_" + currentUser, "No especificada");
            
            tvNombreUsuario.setText(nombreUsuario);
            tvCorreoUsuario.setText(correoUsuario);
            
            // Cargar datos en campos de edición
            etNombreEditar.setText(nombreUsuario);
            etCorreoEditar.setText(correoUsuario);
            etFechaNacimiento.setText(fechaNac);
            
            // Mostrar fecha de registro
            long registrationDate = sharedPreferences.getLong("registration_date_" + currentUser, System.currentTimeMillis());
            String fechaRegistro = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(registrationDate);
            tvFechaRegistro.setText(fechaRegistro);
            
            // Mostrar último acceso
            long loginTime = sharedPreferences.getLong("loginTime", System.currentTimeMillis());
            String ultimoAcceso = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(loginTime);
            tvUltimoAcceso.setText(ultimoAcceso);
        }
    }

    private void loadUserStats() {
        if (currentUser != null && sharedPreferences != null) {
            int comicsFavoritos = sharedPreferences.getInt("comics_favoritos_" + currentUser, 0);
            int heroesSeguidos = sharedPreferences.getInt("heroes_seguidos_" + currentUser, 0);
            int solicitudesRealizadas = sharedPreferences.getInt("solicitudes_realizadas_" + currentUser, 0);
            
            tvComicsFavoritos.setText(String.valueOf(comicsFavoritos));
            tvHeroesSeguidos.setText(String.valueOf(heroesSeguidos));
            tvSolicitudesRealizadas.setText(String.valueOf(solicitudesRealizadas));
        }
    }

    private void loadUserSettings() {
        if (currentUser != null && sharedPreferences != null) {
            boolean notificaciones = sharedPreferences.getBoolean("setting_notificaciones_" + currentUser, true);
            boolean temaOscuro = sharedPreferences.getBoolean("setting_tema_oscuro_" + currentUser, true);
            boolean sonidos = sharedPreferences.getBoolean("setting_sonidos_" + currentUser, true);
            
            switchNotificaciones.setChecked(notificaciones);
            switchTemaOscuro.setChecked(temaOscuro);
            switchSonidos.setChecked(sonidos);
        }
    }

    private void enableEditMode() {
        isEditMode = true;
        layoutVisualizacion.setVisibility(View.GONE);
        layoutEdicion.setVisibility(View.VISIBLE);
        
        // Animación de transición
        layoutEdicion.setAlpha(0f);
        layoutEdicion.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
    }

    private void cancelEdit() {
        isEditMode = false;
        layoutEdicion.setVisibility(View.GONE);
        layoutVisualizacion.setVisibility(View.VISIBLE);
        
        // Restaurar valores originales
        loadUserData();
        
        // Animación de transición
        layoutVisualizacion.setAlpha(0f);
        layoutVisualizacion.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
    }

    private void saveChanges() {
        String nuevoNombre = etNombreEditar.getText().toString().trim();
        String nuevoCorreo = etCorreoEditar.getText().toString().trim();
        String nuevaFecha = etFechaNacimiento.getText().toString().trim();
        
        if (TextUtils.isEmpty(nuevoNombre)) {
            etNombreEditar.setError("El nombre no puede estar vacío");
            return;
        }
        
        if (TextUtils.isEmpty(nuevoCorreo) || !Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
            etCorreoEditar.setError("Ingrese un correo válido");
            return;
        }
        
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name_" + currentUser, nuevoNombre);
            editor.putString("user_" + currentUser, nuevoCorreo);
            editor.putString("birthdate_" + currentUser, nuevaFecha);
            editor.apply();
            
            // Actualizar UI
            tvNombreUsuario.setText(nuevoNombre);
            tvCorreoUsuario.setText(nuevoCorreo);
            
            // Volver a modo visualización
            cancelEdit();
            
            Toast.makeText(getContext(), "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                fechaNacimiento.set(year, month, dayOfMonth);
                etFechaNacimiento.setText(dateFormat.format(fechaNacimiento.getTime()));
            },
            fechaNacimiento.get(Calendar.YEAR),
            fechaNacimiento.get(Calendar.MONTH),
            fechaNacimiento.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveSettingPreference(String setting, boolean value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("setting_" + setting + "_" + currentUser, value);
            editor.apply();
        }
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí", (dialog, which) -> logout())
            .setNegativeButton("No", null)
            .show();
    }

    private void logout() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
        }
        
        // Redirigir a la pantalla de login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEditMode) {
            loadUserData();
            loadUserStats();
            loadUserSettings();
        }
    }
} 