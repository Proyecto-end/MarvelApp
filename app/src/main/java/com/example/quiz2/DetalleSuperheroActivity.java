package com.example.quiz2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import com.example.quiz2.api.MarvelResponse;
import java.util.Arrays;
import java.util.List;
import com.example.quiz2.clases.Superheroe;

public class DetalleSuperheroActivity extends AppCompatActivity {

    public static final String EXTRA_HERO = "extra_hero";

    private Toolbar toolbar;
    private ImageView ivSuperheroeFoto;
    private TextView tvSuperheroeName, tvSuperheroDescription;
    private Chip chipUniverso, chipEstado;
    private TextView tvPopularidadTexto;
    private ProgressBar pbPopularidad;
    private ImageView btnFavorito;
    private MaterialButton btnCompartir;
    private RecyclerView recyclerPoderes, recyclerComics;
    
    private String superheroeName;
    private boolean isFavorite = false;
    private Superheroe superheroe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_superheroe);
        
        initializeViews();
        setupToolbar();
        // Recibir el objeto Superheroe
        superheroe = (Superheroe) getIntent().getSerializableExtra("superheroe_objeto");
        if (superheroe != null) {
            mostrarDatosSuperheroe();
        }
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        ivSuperheroeFoto = findViewById(R.id.heroImage);
        tvSuperheroeName = findViewById(R.id.heroName);
        tvSuperheroDescription = findViewById(R.id.heroDescription);
        chipUniverso = findViewById(R.id.chipUniverso);
        chipEstado = findViewById(R.id.chipEstado);
        tvPopularidadTexto = findViewById(R.id.tvPopularidad);
        pbPopularidad = findViewById(R.id.pbPopularidad);
        btnFavorito = findViewById(R.id.ivFavorite);
        btnCompartir = findViewById(R.id.btnCompartir);
        recyclerPoderes = findViewById(R.id.recyclerPoderes);
        recyclerComics = findViewById(R.id.recyclerComics);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Detalle del Superhéroe");
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void mostrarDatosSuperheroe() {
        tvSuperheroeName.setText(superheroe.getNombre());
        tvSuperheroDescription.setText(superheroe.getDescripcion());
        chipUniverso.setText(superheroe.getUniverso());
        chipEstado.setText(superheroe.getEstado());
        chipEstado.setChipBackgroundColorResource(
            "Activo".equals(superheroe.getEstado()) ? R.color.verde_activo : R.color.rojo_inactivo);
        pbPopularidad.setProgress(superheroe.getPopularidad());
        tvPopularidadTexto.setText(superheroe.getPopularidad() + "% Popularidad");
        if (superheroe.getImagenUrl() != null && !superheroe.getImagenUrl().isEmpty()) {
            Picasso.get()
                .load(superheroe.getImagenUrl())
                .placeholder(R.drawable.placeholder_hero)
                .error(R.drawable.error_hero)
                .into(ivSuperheroeFoto);
        } else {
            ivSuperheroeFoto.setImageResource(R.drawable.placeholder_hero);
        }
        // Puedes agregar aquí más campos si lo deseas (comics, poderes, etc.)
    }

    private void setupHeroSpecificData(String heroName) {
        List<String> poderes;
        List<String> comics;
        
        switch (heroName) {
            case "Spider-Man":
                poderes = Arrays.asList(
                    "Fuerza sobrehumana",
                    "Agilidad y velocidad aumentadas",
                    "Sentido arácnido",
                    "Lanzar telarañas",
                    "Adherencia a superficies",
                    "Reflejos mejorados"
                );
                comics = Arrays.asList(
                    "The Amazing Spider-Man #1",
                    "Spider-Man: No Way Home",
                    "Ultimate Spider-Man",
                    "Spider-Verse",
                    "Web of Spider-Man"
                );
                break;
                
            case "Iron Man":
                poderes = Arrays.asList(
                    "Inteligencia genial",
                    "Armadura Mark avanzada",
                    "Vuelo propulsado",
                    "Repulsores de energía",
                    "Múltiples sistemas de armas",
                    "Análisis en tiempo real"
                );
                comics = Arrays.asList(
                    "Iron Man #1",
                    "Avengers Assemble",
                    "Civil War",
                    "Armor Wars",
                    "Extremis"
                );
                break;
                
            case "Wolverine":
                poderes = Arrays.asList(
                    "Factor de curación acelerado",
                    "Garras de adamantium",
                    "Esqueleto de adamantium",
                    "Sentidos sobrehumanos",
                    "Resistencia sobrehumana",
                    "Instintos animales"
                );
                comics = Arrays.asList(
                    "X-Men Origins: Wolverine",
                    "Logan",
                    "Days of Future Past",
                    "Old Man Logan",
                    "Weapon X"
                );
                break;
                
            case "Captain America":
                poderes = Arrays.asList(
                    "Fuerza sobrehumana",
                    "Agilidad y velocidad aumentadas",
                    "Escudo de vibranium",
                    "Liderazgo nato",
                    "Resistencia aumentada",
                    "Combate experto"
                );
                comics = Arrays.asList(
                    "Captain America #1",
                    "Civil War",
                    "The Winter Soldier",
                    "First Avenger",
                    "Secret Empire"
                );
                break;
                
            case "Doctor Strange":
                poderes = Arrays.asList(
                    "Magia suprema",
                    "Viaje dimensional",
                    "Clarividencia",
                    "Levitación",
                    "Manipulación del tiempo",
                    "Hechizos de protección"
                );
                comics = Arrays.asList(
                    "Doctor Strange #1",
                    "Multiverse of Madness",
                    "The Oath",
                    "Triumph and Torment",
                    "What If...?"
                );
                break;
                
            default:
                poderes = Arrays.asList("Poderes no especificados");
                comics = Arrays.asList("Comics no especificados");
                break;
        }
        
        setupRecyclerView(recyclerPoderes, poderes, "poderes");
        setupRecyclerView(recyclerComics, comics, "comics");
    }

    private void setupRecyclerView(RecyclerView recycler, List<String> items, String tipo) {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new SimpleListAdapter(items, tipo));
    }

    private void setupClickListeners() {
        btnFavorito.setOnClickListener(v -> toggleFavorite());
        btnCompartir.setOnClickListener(v -> shareHero());
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        btnFavorito.setImageResource(isFavorite ? 
            R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        
        String mensaje = isFavorite ? 
            superheroeName + " agregado a favoritos" : 
            superheroeName + " removido de favoritos";
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void shareHero() {
        String shareText = "¡Mira este increíble superhéroe de Marvel: " + superheroeName + "!";
        android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(android.content.Intent.createChooser(shareIntent, "Compartir vía"));
    }

    private class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ViewHolder> {
        private List<String> items;
        private String tipo;

        public SimpleListAdapter(List<String> items, String tipo) {
            this.items = items;
            this.tipo = tipo;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_simple_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String item = items.get(position);
            holder.textView.setText(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(android.view.View view) {
                super(view);
                textView = view.findViewById(R.id.tv_item);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 