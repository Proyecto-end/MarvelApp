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
    private Chip chipUniverso, chipEstado, chipPopular, chipComics;

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
        chipUniverso = view.findViewById(R.id.chipUniverso);
        chipEstado = view.findViewById(R.id.chipEstado);
        chipPopular = view.findViewById(R.id.chipPopular);
        chipComics = view.findViewById(R.id.chipComics);
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
        if (chipTodos == null || chipAvengers == null || chipXMen == null || chipFantasticFour == null || chipUniverso == null || chipEstado == null || chipPopular == null || chipComics == null) {
            return;
        }
        View.OnClickListener chipClickListener = v -> {
            Chip selectedChip = (Chip) v;
            String filtro = selectedChip.getText().toString();
            chipTodos.setChecked(false);
            chipAvengers.setChecked(false);
            chipXMen.setChecked(false);
            chipFantasticFour.setChecked(false);
            chipUniverso.setChecked(false);
            chipEstado.setChecked(false);
            chipPopular.setChecked(false);
            chipComics.setChecked(false);
            selectedChip.setChecked(true);
            filtroActual = filtro;
            filterSuperheroes(etBusqueda.getText().toString(), filtroActual);
        };
        chipTodos.setOnClickListener(chipClickListener);
        chipAvengers.setOnClickListener(chipClickListener);
        chipXMen.setOnClickListener(chipClickListener);
        chipFantasticFour.setOnClickListener(chipClickListener);
        chipUniverso.setOnClickListener(chipClickListener);
        chipEstado.setOnClickListener(chipClickListener);
        chipPopular.setOnClickListener(chipClickListener);
        chipComics.setOnClickListener(chipClickListener);
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
        
        android.util.Log.d("API", "Timestamp: " + timestamp);
        android.util.Log.d("API", "Hash: " + hash);
        android.util.Log.d("API", "Public Key: " + ApiConfig.PUBLIC_KEY);

        MarvelApiClient.getInstance()
            .getApiService()
            .getCharacters(ApiConfig.PUBLIC_KEY, timestamp, hash, 100, 0)
            .enqueue(new Callback<MarvelResponse>() {
                @Override
                public void onResponse(@NonNull Call<MarvelResponse> call, @NonNull Response<MarvelResponse> response) {
                    if (!isAdded()) return; // Verificar si el fragmento está adjunto
                    
                    showLoading(false);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    
                    android.util.Log.d("API", "Código de respuesta: " + response.code());
                    android.util.Log.d("API", "URL de la llamada: " + call.request().url());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        MarvelResponse.Data data = response.body().getData();
                        android.util.Log.d("API", "Data recibida: " + (data != null ? "Sí" : "No"));
                        if (data != null && data.getResults() != null) {
                            android.util.Log.d("API", "Resultados recibidos: " + (data.getResults().isEmpty() ? "Lista vacía" : data.getResults().size() + " héroes"));
                            if (!data.getResults().isEmpty()) {
                                for (MarvelResponse.Character hero : data.getResults()) {
                                    android.util.Log.d("API", "Nombre del héroe: " + hero.getName());
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
                            android.util.Log.e("API", "Data o Results es null");
                            showError("Error en la respuesta de la API");
                        }
                    } else {
                        String errorMessage = "Error al cargar los superhéroes";
                        if (response.code() == 401) {
                            errorMessage = "Error de autenticación con la API de Marvel";
                        } else if (response.code() == 429) {
                            errorMessage = "Límite de solicitudes excedido";
                        }
                        android.util.Log.e("API", "Error en la respuesta: " + errorMessage);
                        showError(errorMessage);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MarvelResponse> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    showLoading(false);
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    android.util.Log.e("API", "Error en la llamada: " + t.getMessage());
                    showError("Error de conexión: " + t.getMessage());
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
            
            // Asignar grupos y popularidad basado en el nombre y descripción
            String nombre = character.getName().toLowerCase();
            String descripcion = character.getDescription().toLowerCase();
            
            // Inicializar popularidad en 0
            int popularidad = 0;
            
            // Spider-Man
            if (nombre.contains("spider") || nombre.contains("peter") || nombre.contains("parker")) {
                hero.addGrupo("Spider-Man");
                popularidad += 100;
            }
            // Iron Man
            if (nombre.contains("iron") || nombre.contains("stark") || nombre.contains("tony")) {
                hero.addGrupo("Avengers");
                popularidad += 100;
            }
            // Captain America
            if (nombre.contains("captain") || nombre.contains("america") || nombre.contains("steve") || nombre.contains("rogers")) {
                hero.addGrupo("Avengers");
                popularidad += 100;
            }
            // Thor
            if (nombre.contains("thor") || nombre.contains("odinson")) {
                hero.addGrupo("Avengers");
                popularidad += 100;
            }
            // Hulk
            if (nombre.contains("hulk") || nombre.contains("banner") || nombre.contains("bruce")) {
                hero.addGrupo("Avengers");
                popularidad += 100;
            }
            // Black Widow
            if (nombre.contains("black") && nombre.contains("widow")) {
                hero.addGrupo("Avengers");
                popularidad += 80;
            }
            // Hawkeye
            if (nombre.contains("hawk") || nombre.contains("eye")) {
                hero.addGrupo("Avengers");
                popularidad += 80;
            }
            // Wolverine
            if (nombre.contains("wolverine") || nombre.contains("logan")) {
                hero.addGrupo("X-Men");
                popularidad += 90;
            }
            // Cyclops
            if (nombre.contains("cyclops") || nombre.contains("scott")) {
                hero.addGrupo("X-Men");
                popularidad += 70;
            }
            // Storm
            if (nombre.contains("storm") || nombre.contains("oro")) {
                hero.addGrupo("X-Men");
                popularidad += 70;
            }
            // Jean Grey
            if (nombre.contains("jean") || nombre.contains("grey") || nombre.contains("phoenix")) {
                hero.addGrupo("X-Men");
                popularidad += 70;
            }
            // Fantastic Four
            if (nombre.contains("mister") && nombre.contains("fantastic")) {
                hero.addGrupo("4 Fantásticos");
                popularidad += 80;
            }
            if (nombre.contains("invisible") && nombre.contains("woman")) {
                hero.addGrupo("4 Fantásticos");
                popularidad += 80;
            }
            if (nombre.contains("human") && nombre.contains("torch")) {
                hero.addGrupo("4 Fantásticos");
                popularidad += 80;
            }
            if (nombre.contains("thing") || nombre.contains("ben") || nombre.contains("grimm")) {
                hero.addGrupo("4 Fantásticos");
                popularidad += 80;
            }
            
            // Añadir popularidad basada en la cantidad de comics
            if (character.getComics() != null && character.getComics().getItems() != null) {
                popularidad += character.getComics().getItems().size() * 2;
            }
            
            hero.setPopularidad(popularidad);
            
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
            
            hero.setEstado(character.isActive() ? "Activo" : "Inactivo");
            
            heroes.add(hero);
        }
        
        // Ordenar héroes por popularidad (de mayor a menor)
        heroes.sort((h1, h2) -> Integer.compare(h2.getPopularidad(), h1.getPopularidad()));
        
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
        List<Superheroe> filtrados = new ArrayList<>();
        for (Superheroe hero : superheroesCompletos) {
            boolean coincide = hero.getNombre().toLowerCase().contains(query.toLowerCase());
            if (!coincide) continue;
            switch (categoria) {
                case "Avengers":
                    coincide = hero.getGrupos() != null && hero.getGrupos().contains("Avengers");
                    break;
                case "X-Men":
                    coincide = hero.getGrupos() != null && hero.getGrupos().contains("X-Men");
                    break;
                case "4 Fantásticos":
                    coincide = hero.getGrupos() != null && hero.getGrupos().contains("4 Fantásticos");
                    break;
                case "Universo 616":
                    coincide = hero.getUniverso() != null && hero.getUniverso().toLowerCase().contains("616");
                    break;
                case "Activos":
                    coincide = hero.getEstado() != null && hero.getEstado().equalsIgnoreCase("Activo");
                    break;
                case "Muy populares":
                    coincide = hero.getPopularidad() >= 80;
                    break;
                case "> 100 cómics":
                    coincide = hero.getComics() != null && hero.getComics().size() > 100;
                    break;
                default:
                    coincide = true;
            }
            if (coincide) filtrados.add(hero);
        }
        adapter.updateList(filtrados);
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
        chipUniverso = null;
        chipEstado = null;
        chipPopular = null;
        chipComics = null;
        adapter = null;
    }
} 