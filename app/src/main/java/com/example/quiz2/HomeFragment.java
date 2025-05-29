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

public class HomeFragment extends Fragment implements SuperheroeAdapter.OnSuperheroeFavoriteListener {

    private RecyclerView recyclerViewSuperheroes;
    private SuperheroeAdapter adapter;
    private EditText etBusqueda;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView lottieLoading;
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

    private void initializeViews(View view) {
        recyclerViewSuperheroes = view.findViewById(R.id.recyclerViewHeroes);
        etBusqueda = view.findViewById(R.id.etSearch);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        lottieLoading = view.findViewById(R.id.loadingAnimation);
        tvBienvenida = view.findViewById(R.id.tvWelcome);
        layoutEstadoVacio = view.findViewById(R.id.layoutEmpty);
        
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
            
            String nombreUsuario = sharedPreferences.getString("name_" + currentUser, currentUser);
            tvBienvenida.setText("¡Bienvenido, " + nombreUsuario + "!");
        }
    }

    private void setupRecyclerView() {
        recyclerViewSuperheroes.setLayoutManager(new LinearLayoutManager(getContext()));
        superheroesCompletos = new ArrayList<>();
        adapter = new SuperheroeAdapter(getContext(), superheroesCompletos);
        adapter.setOnSuperheroeFavoriteListener(this);
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
        swipeRefreshLayout.setColorSchemeResources(
            R.color.marvel_red,
            R.color.marvel_blue,
            R.color.marvel_purple
        );
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadSuperheroes();
            Toast.makeText(getContext(), "Lista actualizada", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSuperheroes() {
        showLoading(true);
        
        // Simular carga de datos
        new android.os.Handler().postDelayed(() -> {
            superheroesCompletos = createMarvelHeroes();
            adapter.updateList(superheroesCompletos);
            updateEstadoLista();
            showLoading(false);
            swipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }

    private List<Superheroe> createMarvelHeroes() {
        List<Superheroe> heroes = new ArrayList<>();
        
        // Spider-Man
        Superheroe spiderMan = new Superheroe();
        spiderMan.setId(1);
        spiderMan.setNombre("Spider-Man");
        spiderMan.setDescripcion("El amigable vecino de Nueva York con poderes arácnidos.");
        spiderMan.setUniverso("Spider-Verse");
        spiderMan.setImagenUrl("https://i.imgur.com/YOgWqMi.jpg");
        spiderMan.setPoderes(Arrays.asList("Fuerza sobrehumana", "Agilidad", "Sentido arácnido", "Lanzar telarañas"));
        spiderMan.setComics(Arrays.asList("Amazing Spider-Man #1", "Spider-Man: No Way Home", "Ultimate Spider-Man"));
        spiderMan.setPopularidad(95);
        spiderMan.setEstado("Vivo");
        heroes.add(spiderMan);

        // Iron Man
        Superheroe ironMan = new Superheroe();
        ironMan.setId(2);
        ironMan.setNombre("Iron Man");
        ironMan.setDescripcion("Tony Stark, genio millonario con armadura tecnológica avanzada.");
        ironMan.setUniverso("Avengers");
        ironMan.setImagenUrl("https://i.imgur.com/xLn5K9h.jpg");
        ironMan.setPoderes(Arrays.asList("Inteligencia genial", "Armadura Mark", "Vuelo", "Repulsores"));
        ironMan.setComics(Arrays.asList("Iron Man #1", "Avengers Assemble", "Civil War"));
        ironMan.setPopularidad(92);
        ironMan.setEstado("Vivo");
        heroes.add(ironMan);

        // Wolverine
        Superheroe wolverine = new Superheroe();
        wolverine.setId(3);
        wolverine.setNombre("Wolverine");
        wolverine.setDescripcion("Mutante con garras de adamantium y factor de curación.");
        wolverine.setUniverso("X-Men");
        wolverine.setImagenUrl("https://i.imgur.com/R2FKLqX.jpg");
        wolverine.setPoderes(Arrays.asList("Garras de adamantium", "Factor de curación", "Sentidos agudizados", "Longevidad"));
        wolverine.setComics(Arrays.asList("Wolverine #1", "X-Men Origins", "Old Man Logan"));
        wolverine.setPopularidad(90);
        wolverine.setEstado("Vivo");
        heroes.add(wolverine);

        return heroes;
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
        if (show) {
            lottieLoading.setVisibility(View.VISIBLE);
            recyclerViewSuperheroes.setVisibility(View.GONE);
            layoutEstadoVacio.setVisibility(View.GONE);
        } else {
            lottieLoading.setVisibility(View.GONE);
            recyclerViewSuperheroes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFavoriteClick(Superheroe superheroe, boolean isFavorite) {
        String mensaje = isFavorite ? 
            superheroe.getNombre() + " agregado a favoritos" : 
            superheroe.getNombre() + " removido de favoritos";
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
} 