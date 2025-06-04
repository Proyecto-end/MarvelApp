package com.example.quiz2.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.quiz2.R;
import com.example.quiz2.MainMarvelActivity;
import com.example.quiz2.api.ApiConfig;
import com.example.quiz2.api.MarvelApiClient;
import com.example.quiz2.api.MarvelResponse;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class SolicitarFragment extends Fragment {
    private AutoCompleteTextView comicSpinner;
    private EditText etCantidad;
    private EditText etMotivo;
    private RadioGroup rgPrioridad;
    private Button solicitarButton;
    private ImageView ivComicPreview;
    private TextView tvComicTitulo;
    private TextView tvComicDescripcion;
    private View cardPreview;
    private ProgressBar loadingAnimation;
    private List<MarvelResponse.Comic> comicsList;
    private MarvelResponse.Comic selectedComic;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private String currentUser;
    private TextView tvComicPrecio;
    private TextView tvComicPaginas;
    private TextView tvComicFecha;
    private TextView tvComicSerie;
    private TextView tvComicEdicion;
    private TextView tvComicCreadores;
    private TextView tvComicPersonajes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitar, container, false);
        
        setupUserInfo();
        if (!isUserLoggedIn()) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            return view;
        }
        
        initializeViews(view);
        setupSpinner();
        loadComics();
        
        return view;
    }

    private void setupUserInfo() {
        if (getActivity() instanceof MainMarvelActivity) {
            MainMarvelActivity mainActivity = (MainMarvelActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
            sharedPreferences = mainActivity.getMarvelSharedPreferences();
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences != null && 
               sharedPreferences.getBoolean("isLoggedIn", false) && 
               !sharedPreferences.getString("currentUser", "").isEmpty();
    }

    private void initializeViews(View view) {
        comicSpinner = view.findViewById(R.id.comicSpinner);
        etCantidad = view.findViewById(R.id.etCantidad);
        etMotivo = view.findViewById(R.id.etMotivo);
        rgPrioridad = view.findViewById(R.id.rgPrioridad);
        solicitarButton = view.findViewById(R.id.btnSolicitar);
        ivComicPreview = view.findViewById(R.id.ivComicPreview);
        tvComicTitulo = view.findViewById(R.id.tvComicTitulo);
        tvComicDescripcion = view.findViewById(R.id.tvComicDescripcion);
        cardPreview = view.findViewById(R.id.cardPreview);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);
        tvComicPrecio = view.findViewById(R.id.tvComicPrecio);
        tvComicPaginas = view.findViewById(R.id.tvComicPaginas);
        tvComicFecha = view.findViewById(R.id.tvComicFecha);
        tvComicSerie = view.findViewById(R.id.tvComicSerie);
        tvComicEdicion = view.findViewById(R.id.tvComicEdicion);
        tvComicCreadores = view.findViewById(R.id.tvComicCreadores);
        tvComicPersonajes = view.findViewById(R.id.tvComicPersonajes);
        solicitarButton.setOnClickListener(v -> solicitarComic());
    }

    private void setupSpinner() {
        comicsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            new ArrayList<>()
        );
        comicSpinner.setAdapter(adapter);

        comicSpinner.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < comicsList.size()) {
                selectedComic = comicsList.get(position);
                showComicPreview(selectedComic);
            }
        });
    }

    private void showComicPreview(MarvelResponse.Comic comic) {
        if (comic == null) return;
        cardPreview.setVisibility(View.VISIBLE);
        tvComicTitulo.setText(comic.getTitle());
        if (comic.getDescription() != null && !comic.getDescription().isEmpty()) {
            tvComicDescripcion.setText(comic.getDescription());
        } else {
            tvComicDescripcion.setText("No hay descripción disponible");
        }
        if (comic.getThumbnail() != null) {
            String imageUrl = comic.getThumbnail().getFullPath();
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_hero)
                .error(R.drawable.error_hero)
                .into(ivComicPreview);
        }
        // Precio
        String precio = "Precio: N/D";
        if (comic.getPrices() != null && !comic.getPrices().isEmpty()) {
            float p = comic.getPrices().get(0).getPrice();
            if (p > 0) precio = String.format("Precio: $%.2f", p);
        }
        tvComicPrecio.setText(precio);
        // Páginas
        tvComicPaginas.setText("Páginas: " + (comic.getPageCount() > 0 ? comic.getPageCount() : "N/D"));
        // Fecha de publicación
        String fecha = "Publicado: N/D";
        if (comic.getDates() != null) {
            for (MarvelResponse.ComicDate d : comic.getDates()) {
                if ("onsaleDate".equals(d.getType()) && d.getDate() != null && !d.getDate().isEmpty()) {
                    fecha = "Publicado: " + d.getDate().substring(0, 10);
                    break;
                }
            }
        }
        tvComicFecha.setText(fecha);
        // Serie
        tvComicSerie.setText("Serie: " + (comic.getSeries() != null && comic.getSeries().getName() != null ? comic.getSeries().getName() : "N/D"));
        // Edición
        tvComicEdicion.setText("Edición: #" + comic.getId());
        // Creadores
        String creadores = "Creadores: N/D";
        if (comic.getCreators() != null && comic.getCreators().getItems() != null && !comic.getCreators().getItems().isEmpty()) {
            List<String> nombres = new ArrayList<>();
            for (MarvelResponse.CreatorSummary c : comic.getCreators().getItems()) {
                nombres.add(c.getName());
            }
            creadores = "Creadores: " + String.join(", ", nombres);
        }
        tvComicCreadores.setText(creadores);
        // Personajes
        String personajes = "Personajes: N/D";
        if (comic.getCharacters() != null && comic.getCharacters().getItems() != null && !comic.getCharacters().getItems().isEmpty()) {
            List<String> nombres = new ArrayList<>();
            for (MarvelResponse.CharacterSummary ch : comic.getCharacters().getItems()) {
                nombres.add(ch.getName());
            }
            personajes = "Personajes: " + String.join(", ", nombres);
        }
        tvComicPersonajes.setText(personajes);
    }

    private void setLoadingVisibility(boolean visible) {
        if (loadingAnimation != null) {
            loadingAnimation.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void loadComics() {
        setLoadingVisibility(true);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = MarvelApiClient.generateHash(timestamp);

        MarvelApiClient.getInstance()
            .getApiService()
            .getComics(ApiConfig.PUBLIC_KEY, timestamp, hash, 100, 0)
            .enqueue(new retrofit2.Callback<com.example.quiz2.api.MarvelComicResponse>() {
                @Override
                public void onResponse(@NonNull Call<com.example.quiz2.api.MarvelComicResponse> call, @NonNull Response<com.example.quiz2.api.MarvelComicResponse> response) {
                    setLoadingVisibility(false);
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.quiz2.api.MarvelComicResponse.Data data = response.body().getData();
                        if (data != null && data.getResults() != null) {
                            comicsList.clear();
                            List<String> comicTitles = new ArrayList<>();
                            for (com.example.quiz2.api.MarvelResponse.Comic comic : data.getResults()) {
                                comicsList.add(comic);
                                comicTitles.add(comic.getTitle());
                            }
                            if (getContext() != null) {
                                adapter.clear();
                                adapter.addAll(comicTitles);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error al cargar los comics", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<com.example.quiz2.api.MarvelComicResponse> call, @NonNull Throwable t) {
                    setLoadingVisibility(false);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void solicitarComic() {
        if (selectedComic == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Por favor seleccione un comic", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String cantidad = etCantidad.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();
        int prioridadId = rgPrioridad.getCheckedRadioButtonId();

        if (cantidad.isEmpty()) {
            etCantidad.setError("Ingrese la cantidad");
            return;
        }

        if (motivo.isEmpty()) {
            etMotivo.setError("Ingrese el motivo");
            return;
        }

        if (prioridadId == -1) {
            Toast.makeText(getContext(), "Seleccione una prioridad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar información del comic seleccionado
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Comic solicitado: ").append(selectedComic.getTitle()).append("\n");
        mensaje.append("Cantidad: ").append(cantidad).append("\n");
        mensaje.append("Motivo: ").append(motivo).append("\n");
        
        String prioridad = "";
        if (prioridadId == R.id.rbBaja) prioridad = "Baja";
        else if (prioridadId == R.id.rbMedia) prioridad = "Media";
        else if (prioridadId == R.id.rbAlta) prioridad = "Alta";
        
        mensaje.append("Prioridad: ").append(prioridad);

        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje.toString(), Toast.LENGTH_LONG).show();
        }
        // Limpiar campos después de solicitar
        comicSpinner.setText("");
        etCantidad.setText("");
        etMotivo.setText("");
        rgPrioridad.clearCheck();
        cardPreview.setVisibility(View.GONE);
        selectedComic = null;
    }
} 