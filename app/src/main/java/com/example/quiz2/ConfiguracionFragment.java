package com.example.quiz2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ConfiguracionFragment extends Fragment {

    private ImageView ivAvatarUsuario;
    private TextView tvNombreUsuario, tvCorreoUsuario;
    private TextView tvComicsFavoritos, tvHeroesSeguidos, tvSolicitudesRealizadas;
    private TextView tvFechaRegistro, tvUltimoAcceso;
    private Switch switchNotificaciones, switchTemaOscuro, switchSonidos;
    private Button btnEditarPerfil, btnGuardarCambios, btnCancelarEdicion, btnCerrarSesion;
    private View layoutVisualizacion, layoutEdicion, layoutEstadisticas;
    private LottieAnimationView lottieAvatar;
    private TextView tvValorNombre, tvValorCorreo, tvValorFechaNacimiento;

    private String currentUser;
    private SharedPreferences sharedPreferences;
    private boolean isEditMode = false;
    private Calendar fechaNacimiento;
    private SimpleDateFormat dateFormat;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private Uri avatarUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        
        initializeViews(view);
        setupUserInfo();
        setupClickListeners(view);
        loadUserData();
        loadUserStats();
        loadUserSettings();
        
        return view;
    }

    private void initializeViews(View view) {
        // Avatar y nombre
        ivAvatarUsuario = view.findViewById(R.id.ivAvatarUsuario);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = view.findViewById(R.id.tvCorreoUsuario);
        
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

        tvValorNombre = view.findViewById(R.id.tvValorNombre);
        tvValorCorreo = view.findViewById(R.id.tvValorCorreo);
        tvValorFechaNacimiento = view.findViewById(R.id.tvValorFechaNacimiento);
    }

    private void setupUserInfo() {
        if (getActivity() instanceof MainMarvelActivity) {
            MainMarvelActivity mainActivity = (MainMarvelActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
            sharedPreferences = mainActivity.getMarvelSharedPreferences();
            
            // Mostrar nombre de usuario
            String nombreCompleto = sharedPreferences.getString("userName", currentUser);
            tvNombreUsuario.setText(nombreCompleto);
            
            // Mostrar correo
            String correo = sharedPreferences.getString("userEmail", currentUser + "@marvel.com");
            tvCorreoUsuario.setText(correo);
            
            // Mostrar fecha de registro
            String fechaRegistro = sharedPreferences.getString("registerDate", "01/01/2024");
            tvFechaRegistro.setText(fechaRegistro);
            
            // Mostrar último acceso
            String ultimoAcceso = sharedPreferences.getString("lastAccess", "01/01/2024 12:00");
            tvUltimoAcceso.setText(ultimoAcceso);
        }
    }

    private void loadUserData() {
        if (currentUser != null && sharedPreferences != null) {
            String nombreUsuario = sharedPreferences.getString("userName", currentUser);
            String correoUsuario = sharedPreferences.getString("userEmail", currentUser + "@marvel.com");
            String fechaNac = sharedPreferences.getString("birthdate", "No especificada");
            tvNombreUsuario.setText(nombreUsuario);
            tvCorreoUsuario.setText(correoUsuario);
            // Mostrar valores reales en la sección de información personal
            tvValorNombre.setText(nombreUsuario);
            tvValorCorreo.setText(correoUsuario);
            tvValorFechaNacimiento.setText(fechaNac);
            // Mostrar fecha de registro
            long registrationDate = sharedPreferences.getLong("registration_date_" + currentUser, System.currentTimeMillis());
            String fechaRegistro = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(registrationDate);
            tvFechaRegistro.setText(fechaRegistro);
            // Mostrar último acceso
            long loginTime = sharedPreferences.getLong("loginTime", System.currentTimeMillis());
            String ultimoAcceso = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(loginTime);
            tvUltimoAcceso.setText(ultimoAcceso);
            // Cargar avatar si existe y es seguro
            String avatarUriString = sharedPreferences.getString("avatarUri", null);
            if (avatarUriString != null) {
                try {
                    File avatarFile = new File(Uri.parse(avatarUriString).getPath());
                    if (avatarFile.exists() && avatarFile.length() > 0) {
                        avatarUri = Uri.fromFile(avatarFile);
                        ivAvatarUsuario.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ivAvatarUsuario.setImageURI(null); // Limpiar la imagen actual
                        ivAvatarUsuario.setImageURI(avatarUri);
                    } else {
                        ivAvatarUsuario.setImageResource(R.drawable.ic_person);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ivAvatarUsuario.setImageResource(R.drawable.ic_person);
                }
            } else {
                ivAvatarUsuario.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void loadUserStats() {
        // Cargar estadísticas del usuario
        int comicsFavoritos = sharedPreferences.getInt("favoriteComics", 0);
        int heroesSeguidos = sharedPreferences.getInt("followedHeroes", 0);
        int solicitudes = sharedPreferences.getInt("requests", 0);
        
        tvComicsFavoritos.setText(String.valueOf(comicsFavoritos));
        tvHeroesSeguidos.setText(String.valueOf(heroesSeguidos));
        tvSolicitudesRealizadas.setText(String.valueOf(solicitudes));
    }

    private void loadUserSettings() {
        // Cargar configuraciones guardadas
        boolean notificaciones = sharedPreferences.getBoolean("notifications", true);
        boolean temaOscuro = sharedPreferences.getBoolean("darkTheme", true);
        boolean sonidos = sharedPreferences.getBoolean("sounds", true);
        
        switchNotificaciones.setChecked(notificaciones);
        switchTemaOscuro.setChecked(temaOscuro);
        switchSonidos.setChecked(sonidos);
    }

    private void saveUserSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", switchNotificaciones.isChecked());
        editor.putBoolean("darkTheme", switchTemaOscuro.isChecked());
        editor.putBoolean("sounds", switchSonidos.isChecked());
        editor.apply();
    }

    private void setupClickListeners(View view) {
        btnEditarPerfil.setOnClickListener(v -> toggleEditMode(true));
        btnCancelarEdicion.setOnClickListener(v -> toggleEditMode(false));
        btnGuardarCambios.setOnClickListener(v -> saveChanges());
        btnCerrarSesion.setOnClickListener(v -> logout());
        ivAvatarUsuario.setOnClickListener(v -> openImagePicker());
        // Configurar switches
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> saveUserSettings());
        switchTemaOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> saveUserSettings());
        switchSonidos.setOnCheckedChangeListener((buttonView, isChecked) -> saveUserSettings());
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        layoutVisualizacion.setVisibility(enable ? View.GONE : View.VISIBLE);
        layoutEdicion.setVisibility(enable ? View.VISIBLE : View.GONE);
        
        if (enable) {
            tvNombreUsuario.setText(tvNombreUsuario.getText());
            tvCorreoUsuario.setText(tvCorreoUsuario.getText());
        }
    }

    private void saveChanges() {
        String nuevoNombre = tvNombreUsuario.getText().toString().trim();
        String nuevoCorreo = tvCorreoUsuario.getText().toString().trim();
        if (TextUtils.isEmpty(nuevoNombre) || TextUtils.isEmpty(nuevoCorreo)) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
            Toast.makeText(getContext(), "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }
        // Guardar cambios
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", nuevoNombre);
        editor.putString("userEmail", nuevoCorreo);
        editor.apply();
        // Actualizar UI
        tvNombreUsuario.setText(nuevoNombre);
        tvCorreoUsuario.setText(nuevoCorreo);
        toggleEditMode(false);
        Toast.makeText(getContext(), "Cambios guardados", Toast.LENGTH_SHORT).show();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                fechaNacimiento.set(year, month, dayOfMonth);
                tvValorFechaNacimiento.setText(dateFormat.format(fechaNacimiento.getTime()));
            },
            fechaNacimiento.get(Calendar.YEAR),
            fechaNacimiento.get(Calendar.MONTH),
            fechaNacimiento.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void logout() {
        // Limpiar preferencias
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        // Redirigir al login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void openImagePicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Se necesita permiso para acceder a la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                // Verificar que podemos acceder a la imagen
                getContext().getContentResolver().openInputStream(selectedImageUri);
                // Copiar la imagen a almacenamiento interno
                Uri internalUri = copyImageToInternalStorage(selectedImageUri);
                if (internalUri != null) {
                    avatarUri = internalUri;
                    // Asegurarnos de que la imagen se muestre correctamente
                    ivAvatarUsuario.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivAvatarUsuario.setImageURI(null); // Limpiar la imagen actual
                    ivAvatarUsuario.setImageURI(avatarUri);
                    sharedPreferences.edit().putString("avatarUri", avatarUri.toString()).apply();
                    Toast.makeText(getContext(), "Imagen actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri copyImageToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;
            
            // Crear un nombre único para el archivo
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new java.util.Date());
            String imageFileName = "AVATAR_" + timeStamp + ".jpg";
            File avatarFile = new File(getContext().getFilesDir(), imageFileName);
            
            FileOutputStream outputStream = new FileOutputStream(avatarFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            
            // Verificar que el archivo se creó correctamente
            if (avatarFile.exists() && avatarFile.length() > 0) {
                return Uri.fromFile(avatarFile);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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