package com.example.quiz2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.example.quiz2.api.ApiConfig;
import com.example.quiz2.api.MarvelApiClient;
import com.example.quiz2.api.MarvelResponse;
import com.example.quiz2.models.Superhero;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.quiz2.fragments.SolicitarFragment;

public class MainMarvelActivity extends AppCompatActivity {

    private static final String TAG = "MainMarvelActivity";
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_marvel);

        initializeViews();
        setupToolbar();
        setupBottomNavigation();
        
        sharedPreferences = getSharedPreferences("MarvelUserPrefs", MODE_PRIVATE);
        currentUser = sharedPreferences.getString("currentUser", "");
        
        // Verificar si el usuario está logueado
        if (currentUser.isEmpty()) {
            redirectToLogin();
            return;
        }
        
        // Cargar fragmento guardado o HomeFragment por defecto
        if (savedInstanceState == null) {
            int lastFragment = sharedPreferences.getInt("lastFragment", R.id.nav_home);
            Fragment selectedFragment;
            if (lastFragment == R.id.nav_configuracion) {
                selectedFragment = new ConfiguracionFragment();
            } else if (lastFragment == R.id.nav_solicitar) {
                selectedFragment = new SolicitarFragment();
            } else {
                selectedFragment = new HomeFragment();
            }
            loadFragment(selectedFragment);
            bottomNavigation.setSelectedItemId(lastFragment);
        }

        // Ejemplo de llamada a la API
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = MarvelApiClient.generateHash(timestamp);

        MarvelApiClient.getInstance()
            .getApiService()
            .getCharacters(ApiConfig.PUBLIC_KEY, timestamp, hash, 20, 0)
            .enqueue(new Callback<MarvelResponse>() {
                @Override
                public void onResponse(Call<MarvelResponse> call, Response<MarvelResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MarvelResponse.Data data = response.body().getData();
                        if (data != null && data.getResults() != null) {
                            for (MarvelResponse.Character character : data.getResults()) {
                                Log.d(TAG, "Personaje: " + character.getName());
                                Log.d(TAG, "Descripción: " + character.getDescription());
                                if (character.getThumbnail() != null) {
                                    Log.d(TAG, "Imagen: " + character.getThumbnail().getFullPath());
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error en la respuesta: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<MarvelResponse> call, Throwable t) {
                    Log.e(TAG, "Error en la llamada: " + t.getMessage());
                }
            });
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!isUserLoggedIn()) {
                    redirectToLogin();
                    return false;
                }

                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_solicitar) {
                    selectedFragment = new SolicitarFragment();
                } else if (itemId == R.id.nav_configuracion) {
                    selectedFragment = new ConfiguracionFragment();
                }

                if (selectedFragment != null) {
                    // Guardar el último fragmento seleccionado
                    sharedPreferences.edit().putInt("lastFragment", itemId).apply();
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences != null && 
               sharedPreferences.getBoolean("isLoggedIn", false) && 
               !sharedPreferences.getString("currentUser", "").isEmpty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUserLoggedIn()) {
            redirectToLogin();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_search) {
            Toast.makeText(this, "Función de búsqueda", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_notifications) {
            Toast.makeText(this, "No tienes notificaciones nuevas", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí", (dialog, which) -> logout())
            .setNegativeButton("No", null)
            .setIcon(R.drawable.ic_logout)
            .show();
    }

    private void logout() {
        // Limpiar sesión
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("currentUser");
        editor.apply();
        
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Verificar si hay fragments en el back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            // Mostrar diálogo de confirmación para salir
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Estás seguro de que quieres salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    super.onBackPressed();
                    finishAffinity(); // Cerrar completamente la app
                })
                .setNegativeButton("No", null)
                .show();
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public SharedPreferences getMarvelSharedPreferences() {
        return sharedPreferences;
    }
} 