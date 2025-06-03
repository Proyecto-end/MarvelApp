package com.example.quiz2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quiz2.adaptador.SuperheroeAdapter;
import com.example.quiz2.clases.Superheroe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.quiz2.api.ApiConfig;
import com.example.quiz2.api.MarvelApiClient;
import com.example.quiz2.api.MarvelResponse;
import com.example.quiz2.MainMarvelActivity;
import android.content.Intent;
import java.lang.StringBuilder;

public class HomeFragment extends Fragment implements SuperheroeAdapter.OnSuperheroeClickListener {

    private RecyclerView recyclerViewSuperheroes;
    private SuperheroeAdapter adapter;
    private EditText etBusqueda;
    private SwipeRefreshLayout swipeRefreshLayout;
    public LottieAnimationView lottieLoading;
    private TextView tvBienvenida;
    private View layoutEstadoVacio;
    
    private List<Superheroe> superheroesCompletos;
    private String currentUser;
    private SharedPreferences sharedPreferences;
    private String filtroActual = "Todos";
    
    // Chips para filtros
    private Chip chipTodos, chipAvengers, chipXMen, chipFantasticFour;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initializeViews(view);
        setupUserInfo();
        setupRecyclerView();
        setupSearchAndFilters();
        setupSwipeRefresh();
        loadSuperheroes();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        loadSuperheroes();
        setupSwipeRefresh();
    }

    private void initializeViews(View view) {
        recyclerViewSuperheroes = view.findViewById(R.id.recyclerViewHeroes);
        etBusqueda = view.findViewById(R.id.etSearch);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        lottieLoading = view.findViewById(R.id.loadingAnimation);
        tvBienvenida = view.findViewById(R.id.tvWelcome);
        layoutEstadoVacio = view.findViewById(R.id.layoutEmpty);
        
        // Configurar SwipeRefreshLayout
        if (swipeRefreshLayout != null && getContext() != null) {
            try {
                swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                    getContext().getResources().getColor(R.color.marvel_medium_gray)
                );
                swipeRefreshLayout.setColorSchemeColors(
                    getContext().getResources().getColor(R.color.marvel_red),
                    getContext().getResources().getColor(R.color.marvel_blue),
                    getContext().getResources().getColor(R.color.marvel_purple)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Configurar LottieAnimationView
        if (lottieLoading != null) {
            try {
                lottieLoading.setAnimation(R.raw.loading_animation);
                lottieLoading.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Inicializar chips para filtros
        chipTodos = view.findViewById(R.id.chipTodos);
        chipAvengers = view.findViewById(R.id.chipAvengers);
        chipXMen = view.findViewById(R.id.chipXMen);
        chipFantasticFour = view.findViewById(R.id.chipFantasticFour);
    }

    private void setupUserInfo() {
        if (getActivity() instanceof MainMarvelActivity) {
            MainMarvelActivity mainActivity = (MainMarvelActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
            sharedPreferences = mainActivity.getMarvelSharedPreferences();
            
            String nombreCompleto = sharedPreferences.getString("userName", "");
            if (nombreCompleto.isEmpty()) {
                nombreCompleto = currentUser;
            }
            tvBienvenida.setText("¡Bienvenido, " + nombreCompleto + "!");
        }
    }

    private void setupRecyclerView() {
        recyclerViewSuperheroes.setLayoutManager(new LinearLayoutManager(getContext()));
        superheroesCompletos = new ArrayList<>();
        adapter = new SuperheroeAdapter(getContext(), superheroesCompletos, this);
        recyclerViewSuperheroes.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        // Configurar búsqueda
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSuperheroes(s.toString(), filtroActual);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar filtros con chips
        setupFilterChips();
    }

    private void setupFilterChips() {
        // Verificar que todos los chips existan antes de configurar listeners
        if (chipTodos == null || chipAvengers == null || chipXMen == null || chipFantasticFour == null) {
            // Si algún chip no existe, no configurar filtros avanzados
            return;
        }
        
        // Configurar listener para cada chip
        View.OnClickListener chipClickListener = v -> {
            Chip selectedChip = (Chip) v;
            String filtro = selectedChip.getText().toString();
            
            // Desmarcar todos los chips
            chipTodos.setChecked(false);
            chipAvengers.setChecked(false);
            chipXMen.setChecked(false);
            chipFantasticFour.setChecked(false);
            
            // Marcar el chip seleccionado
            selectedChip.setChecked(true);
            
            // Actualizar filtro y lista
            filtroActual = filtro;
            filterSuperheroes(etBusqueda.getText().toString(), filtroActual);
        };

        // Asignar listeners
        chipTodos.setOnClickListener(chipClickListener);
        chipAvengers.setOnClickListener(chipClickListener);
        chipXMen.setOnClickListener(chipClickListener);
        chipFantasticFour.setOnClickListener(chipClickListener);
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null && getContext() != null) {
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
                getContext().getResources().getColor(R.color.marvel_medium_gray)
            );
            swipeRefreshLayout.setColorSchemeColors(
                getContext().getResources().getColor(R.color.marvel_red),
                getContext().getResources().getColor(R.color.marvel_blue),
                getContext().getResources().getColor(R.color.marvel_purple)
            );
            
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadSuperheroes();
                Toast.makeText(getContext(), "Lista actualizada", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void loadSuperheroes() {
        showLoading(true);
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = MarvelApiClient.generateHash(timestamp);

        MarvelApiClient.getInstance()
            .getApiService()
            .getCharacters(ApiConfig.PUBLIC_KEY, timestamp, hash, 20, 0)
            .enqueue(new Callback<MarvelResponse>() {
                @Override
                public void onResponse(@NonNull Call<MarvelResponse> call, @NonNull Response<MarvelResponse> response) {
                    if (!isAdded()) return; // Verificar si el fragmento está adjunto
                    
                    showLoading(false);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    
                    if (response.isSuccessful() && response.body() != null) {
                        MarvelResponse.Data data = response.body().getData();
                        if (data != null && data.getResults() != null && !data.getResults().isEmpty()) {
                            android.util.Log.d("API", "Héroes recibidos: " + data.getResults().size());
                            if (getContext() != null) {
                                android.widget.Toast.makeText(getContext(), 
                                    "Héroes recibidos: " + data.getResults().size(), 
                                    android.widget.Toast.LENGTH_SHORT).show();
                            }
                            superheroesCompletos = convertToSuperheroes(data.getResults());
                            if (adapter != null) {
                                adapter.updateList(superheroesCompletos);
                                updateEstadoLista();
                            }
                        } else {
                            showError("No se encontraron superhéroes");
                        }
                    } else {
                        String errorMessage = "Error al cargar los superhéroes";
                        if (response.code() == 401) {
                            errorMessage = "Error de autenticación con la API de Marvel";
                        } else if (response.code() == 429) {
                            errorMessage = "Límite de solicitudes excedido";
                        }
                        showError(errorMessage);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MarvelResponse> call, @NonNull Throwable t) {
                    if (!isAdded()) return; // Verificar si el fragmento está adjunto
                    
                    showLoading(false);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    
                    String errorMessage = "Error de conexión";
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "No hay conexión a internet";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        errorMessage = "Tiempo de espera agotado";
                    }
                    showError(errorMessage + ": " + t.getMessage());
                }
            });
    }

    private List<Superheroe> convertToSuperheroes(List<MarvelResponse.Character> characters) {
        List<Superheroe> heroes = new ArrayList<>();
        
        for (MarvelResponse.Character character : characters) {
            Superheroe hero = new Superheroe();
            hero.setId(character.getId());
            hero.setNombre(character.getName());
            hero.setDescripcion(character.getDescription());
            hero.setUniverso(character.getUniverse());
            
            // Asignar fecha de primera aparición
            if (character.getModified() != null) {
                hero.setPrimeraAparicion(character.getModified().substring(0, 10));
            }
            
            // Asignar grupos basado en el nombre y descripción
            String nombre = character.getName().toLowerCase();
            String descripcion = character.getDescription().toLowerCase();
            
            if (nombre.contains("spider") || nombre.contains("peter") || nombre.contains("parker")) {
                hero.addGrupo("Spider-Man");
            }
            if (nombre.contains("iron") || nombre.contains("stark") || nombre.contains("tony")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("captain") || nombre.contains("america") || nombre.contains("steve") || nombre.contains("rogers")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("thor") || nombre.contains("odinson")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("hulk") || nombre.contains("banner") || nombre.contains("bruce")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("black") && nombre.contains("widow")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("hawk") || nombre.contains("eye")) {
                hero.addGrupo("Avengers");
            }
            if (nombre.contains("wolverine") || nombre.contains("logan")) {
                hero.addGrupo("X-Men");
            }
            if (nombre.contains("cyclops") || nombre.contains("scott")) {
                hero.addGrupo("X-Men");
            }
            if (nombre.contains("storm") || nombre.contains("oro")) {
                hero.addGrupo("X-Men");
            }
            if (nombre.contains("jean") || nombre.contains("grey") || nombre.contains("phoenix")) {
                hero.addGrupo("X-Men");
            }
            if (nombre.contains("mister") && nombre.contains("fantastic")) {
                hero.addGrupo("4 Fantásticos");
            }
            if (nombre.contains("invisible") && nombre.contains("woman")) {
                hero.addGrupo("4 Fantásticos");
            }
            if (nombre.contains("human") && nombre.contains("torch")) {
                hero.addGrupo("4 Fantásticos");
            }
            if (nombre.contains("thing") || nombre.contains("ben") || nombre.contains("grimm")) {
                hero.addGrupo("4 Fantásticos");
            }
            
            if (character.getThumbnail() != null) {
                hero.setImagenUrl(character.getThumbnail().getFullPath());
            }
            
            // Obtener comics del personaje
            if (character.getComics() != null && character.getComics().getItems() != null) {
                List<String> comicTitles = new ArrayList<>();
                for (MarvelResponse.ComicSummary comic : character.getComics().getItems()) {
                    String fecha = comic.getModified() != null ? " (" + comic.getModified().substring(0, 10) + ")" : "";
                    comicTitles.add(comic.getName() + fecha);
                }
                hero.setComics(comicTitles);
            }
            // Obtener series del personaje
            if (character.getSeries() != null && character.getSeries().getItems() != null) {
                List<String> seriesTitles = new ArrayList<>();
                for (MarvelResponse.ComicSummary serie : character.getSeries().getItems()) {
                    String fecha = serie.getModified() != null ? " (" + serie.getModified().substring(0, 10) + ")" : "";
                    seriesTitles.add(serie.getName() + fecha);
                }
                hero.setSeries(seriesTitles);
            }
            // Obtener historias del personaje
            if (character.getStories() != null && character.getStories().getItems() != null) {
                List<String> storyTitles = new ArrayList<>();
                for (MarvelResponse.ComicSummary story : character.getStories().getItems()) {
                    String fecha = story.getModified() != null ? " (" + story.getModified().substring(0, 10) + ")" : "";
                    storyTitles.add(story.getName() + fecha);
                }
                hero.setStories(storyTitles);
            }
            // Obtener eventos del personaje
            if (character.getEvents() != null && character.getEvents().getItems() != null) {
                List<String> eventTitles = new ArrayList<>();
                for (MarvelResponse.ComicSummary event : character.getEvents().getItems()) {
                    String fecha = event.getModified() != null ? " (" + event.getModified().substring(0, 10) + ")" : "";
                    eventTitles.add(event.getName() + fecha);
                }
                hero.setEvents(eventTitles);
            }
            
            hero.setPopularidad(character.getPopularity());
            hero.setEstado(character.isActive() ? "Activo" : "Inactivo");
            
            heroes.add(hero);
        }
        
        return heroes;
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        layoutEstadoVacio.setVisibility(View.VISIBLE);
        recyclerViewSuperheroes.setVisibility(View.GONE);
    }

    private void filterSuperheroes(String query, String categoria) {
        adapter.filter(query, categoria);
        updateEstadoLista();
    }

    private void updateEstadoLista() {
        if (adapter.getItemCount() == 0) {
            layoutEstadoVacio.setVisibility(View.VISIBLE);
            recyclerViewSuperheroes.setVisibility(View.GONE);
        } else {
            layoutEstadoVacio.setVisibility(View.GONE);
            recyclerViewSuperheroes.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        if (getActivity() == null || !isAdded()) return;
        
        if (lottieLoading != null) {
            try {
                if (show) {
                    lottieLoading.setVisibility(android.view.View.VISIBLE);
                    lottieLoading.resumeAnimation();
                } else {
                    lottieLoading.setVisibility(android.view.View.GONE);
                    lottieLoading.cancelAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (recyclerViewSuperheroes != null) {
            recyclerViewSuperheroes.setVisibility(show ? android.view.View.GONE : android.view.View.VISIBLE);
        }
        
        if (layoutEstadoVacio != null) {
            layoutEstadoVacio.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void onSuperheroeClick(Superheroe superheroe) {
        // Navegar a la pantalla de detalles
        Intent intent = new Intent(requireContext(), DetalleSuperheroActivity.class);
        intent.putExtra("superheroe_objeto", superheroe);
        startActivity(intent);
    }

    @Override
    public void onFavoritoClick(Superheroe superheroe) {
        // Actualizar el estado de favorito
        superheroe.setFavorito(!superheroe.isFavorito());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onVerMasClick(Superheroe superheroe) {
        // Navegar a la pantalla de detalles
        Intent intent = new Intent(requireContext(), DetalleSuperheroActivity.class);
        intent.putExtra("superheroe_objeto", superheroe);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerViewSuperheroes = null;
        etBusqueda = null;
        swipeRefreshLayout = null;
        lottieLoading = null;
        tvBienvenida = null;
        layoutEstadoVacio = null;
        superheroesCompletos = null;
        currentUser = null;
        sharedPreferences = null;
        filtroActual = null;
        chipTodos = null;
        chipAvengers = null;
        chipXMen = null;
        chipFantasticFour = null;
        adapter = null;
    }
} 