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
    private MaterialButton btnCompartir;
    private RecyclerView recyclerComics;
    
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
        ivSuperheroeFoto = findViewById(R.id.ivHeroImage);
        tvSuperheroeName = findViewById(R.id.tvHeroName);
        tvSuperheroDescription = findViewById(R.id.tvDescription);
        chipUniverso = findViewById(R.id.chipUniverso);
        chipEstado = findViewById(R.id.chipEstado);
        tvPopularidadTexto = findViewById(R.id.tvPopularidad);
        pbPopularidad = findViewById(R.id.pbPopularidad);
        btnCompartir = findViewById(R.id.btnCompartir);
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
        superheroeName = superheroe.getNombre();
        tvSuperheroeName.setText(superheroeName);
        tvSuperheroDescription.setText(superheroe.getDescripcion() != null && !superheroe.getDescripcion().isEmpty() ? superheroe.getDescripcion() : "Sin descripción disponible");
        chipUniverso.setText(superheroe.getUniverso() != null ? superheroe.getUniverso() : "-");
        chipEstado.setText(superheroe.getEstado() != null ? superheroe.getEstado() : "-");
        chipEstado.setChipBackgroundColorResource(
            "Activo".equalsIgnoreCase(superheroe.getEstado()) ? R.color.verde_activo : R.color.rojo_inactivo);
        pbPopularidad.setProgress(superheroe.getPopularidad());
        tvPopularidadTexto.setText(superheroe.getPopularidad() + "% Popularidad");

        // Número de cómics
        TextView tvComicsCount = findViewById(R.id.tvComicsCount);
        if (tvComicsCount != null) {
            int comicsCount = (superheroe.getComics() != null) ? superheroe.getComics().size() : 0;
            tvComicsCount.setText(String.valueOf(comicsCount));
        }

        // Debut
        TextView tvPrimeraAparicion = findViewById(R.id.tvPrimeraAparicion);
        if (tvPrimeraAparicion != null) {
            String debut = (superheroe.getPrimeraAparicion() != null && !superheroe.getPrimeraAparicion().isEmpty()) ? superheroe.getPrimeraAparicion() : "-";
            tvPrimeraAparicion.setText(debut);
        }

        if (superheroe.getImagenUrl() != null && !superheroe.getImagenUrl().isEmpty()) {
            Picasso.get()
                .load(superheroe.getImagenUrl())
                .placeholder(R.drawable.placeholder_hero)
                .error(R.drawable.error_hero)
                .into(ivSuperheroeFoto);
        } else {
            ivSuperheroeFoto.setImageResource(R.drawable.placeholder_hero);
        }

        // Mostrar cómics si existen
        if (superheroe.getComics() != null && !superheroe.getComics().isEmpty()) {
            setupRecyclerView(recyclerComics, superheroe.getComics(), "comics");
            recyclerComics.setVisibility(View.VISIBLE);
        } else {
            recyclerComics.setVisibility(View.GONE);
        }

        // Mostrar series si existen
        RecyclerView recyclerSeries = findViewById(R.id.recyclerSeries);
        if (superheroe.getSeries() != null && !superheroe.getSeries().isEmpty()) {
            setupRecyclerView(recyclerSeries, superheroe.getSeries(), "series");
            recyclerSeries.setVisibility(View.VISIBLE);
        } else {
            recyclerSeries.setVisibility(View.GONE);
        }
        // Mostrar historias si existen
        RecyclerView recyclerStories = findViewById(R.id.recyclerStories);
        if (superheroe.getStories() != null && !superheroe.getStories().isEmpty()) {
            setupRecyclerView(recyclerStories, superheroe.getStories(), "stories");
            recyclerStories.setVisibility(View.VISIBLE);
        } else {
            recyclerStories.setVisibility(View.GONE);
        }
        // Mostrar eventos si existen
        RecyclerView recyclerEvents = findViewById(R.id.recyclerEvents);
        if (superheroe.getEvents() != null && !superheroe.getEvents().isEmpty()) {
            setupRecyclerView(recyclerEvents, superheroe.getEvents(), "events");
            recyclerEvents.setVisibility(View.VISIBLE);
        } else {
            recyclerEvents.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView(RecyclerView recycler, List<String> items, String tipo) {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new SimpleListAdapter(items, tipo));
    }

    private void setupClickListeners() {
        btnCompartir.setOnClickListener(v -> shareHero());
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