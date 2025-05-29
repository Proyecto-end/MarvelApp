package com.example.quiz2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.example.quiz2.clases.Comic;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SolicitarFragment extends Fragment {

    private Spinner spinnerComics;
    private EditText etCantidad, etMotivo;
    private RadioGroup rgPrioridad;
    private Button btnSolicitar;
    private ImageView ivPreviewComic;
    private TextView tvTituloPreview, tvDescripcionPreview;
    private LottieAnimationView lottieLoading;
    private View layoutPreview;

    private List<Comic> comicsDisponibles;
    private ArrayAdapter<String> spinnerAdapter;
    private Comic comicSeleccionado;
    private String currentUser;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitar, container, false);
        
        initializeViews(view);
        setupUserInfo();
        setupSpinner();
        setupClickListeners();
        loadComics();
        
        return view;
    }

    private void initializeViews(View view) {
        spinnerComics = view.findViewById(R.id.spinnerComics);
        etCantidad = view.findViewById(R.id.etCantidad);
        etMotivo = view.findViewById(R.id.etMotivo);
        rgPrioridad = view.findViewById(R.id.rgPrioridad);
        btnSolicitar = view.findViewById(R.id.btnSolicitar);
        ivPreviewComic = view.findViewById(R.id.ivComicPreview);
        tvTituloPreview = view.findViewById(R.id.tvComicTitulo);
        tvDescripcionPreview = view.findViewById(R.id.tvComicDescripcion);
        lottieLoading = view.findViewById(R.id.loadingAnimation);
        layoutPreview = view.findViewById(R.id.cardPreview);
    }

    private void setupUserInfo() {
        if (getActivity() instanceof MainMarvelActivity) {
            MainMarvelActivity mainActivity = (MainMarvelActivity) getActivity();
            currentUser = mainActivity.getCurrentUser();
            sharedPreferences = mainActivity.getMarvelSharedPreferences();
        }
    }

    private void setupSpinner() {
        List<String> titulosComics = new ArrayList<>();
        titulosComics.add("Seleccionar comic...");
        
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, titulosComics);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComics.setAdapter(spinnerAdapter);

        spinnerComics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && comicsDisponibles != null && position <= comicsDisponibles.size()) {
                    comicSeleccionado = comicsDisponibles.get(position - 1);
                    showComicPreview(comicSeleccionado);
                } else {
                    comicSeleccionado = null;
                    hideComicPreview();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                comicSeleccionado = null;
                hideComicPreview();
            }
        });
    }

    private void setupClickListeners() {
        btnSolicitar.setOnClickListener(v -> validateAndSubmitRequest());
    }

    private void loadComics() {
        lottieLoading.setVisibility(View.VISIBLE);
        
        // Simular carga de comics
        new android.os.Handler().postDelayed(() -> {
            comicsDisponibles = createMarvelComics();
            updateSpinner();
            lottieLoading.setVisibility(View.GONE);
        }, 1000);
    }

    private List<Comic> createMarvelComics() {
        List<Comic> comics = new ArrayList<>();

        // Amazing Spider-Man
        Comic spiderMan = new Comic();
        spiderMan.setId(1);
        spiderMan.setTitulo("The Amazing Spider-Man #1");
        spiderMan.setDescripcion("El debut del arácnido más famoso de Marvel en una nueva serie llena de acción y aventuras.");
        spiderMan.setImagenUrl("https://i.imgur.com/SpiderMan1.jpg");
        spiderMan.setFechaPublicacion("15/03/2024");
        spiderMan.setStock(25);
        spiderMan.setPrecio(4.99);
        comics.add(spiderMan);

        // Avengers
        Comic avengers = new Comic();
        avengers.setId(2);
        avengers.setTitulo("Avengers: Endgame Special Edition");
        avengers.setDescripcion("La conclusión épica de la saga del infinito con arte exclusivo y escenas inéditas.");
        avengers.setImagenUrl("https://i.imgur.com/Avengers1.jpg");
        avengers.setFechaPublicacion("22/04/2024");
        avengers.setStock(15);
        avengers.setPrecio(6.99);
        comics.add(avengers);

        // X-Men
        Comic xmen = new Comic();
        xmen.setId(3);
        xmen.setTitulo("X-Men: Days of Future Past");
        xmen.setDescripcion("Los mutantes enfrentan un futuro distópico en esta historia clásica reimaginada.");
        xmen.setImagenUrl("https://i.imgur.com/XMen1.jpg");
        xmen.setFechaPublicacion("10/05/2024");
        xmen.setStock(30);
        xmen.setPrecio(5.99);
        comics.add(xmen);

        // Iron Man
        Comic ironMan = new Comic();
        ironMan.setId(4);
        ironMan.setTitulo("Iron Man: Armor Wars");
        ironMan.setDescripcion("Tony Stark debe recuperar su tecnología robada en esta emocionante aventura.");
        ironMan.setImagenUrl("https://i.imgur.com/IronMan1.jpg");
        ironMan.setFechaPublicacion("05/06/2024");
        ironMan.setStock(20);
        ironMan.setPrecio(5.49);
        comics.add(ironMan);

        // Doctor Strange
        Comic drStrange = new Comic();
        drStrange.setId(5);
        drStrange.setTitulo("Doctor Strange: Multiverse Madness");
        drStrange.setDescripcion("El Hechicero Supremo explora dimensiones peligrosas y realidades alternativas.");
        drStrange.setImagenUrl("https://i.imgur.com/DrStrange1.jpg");
        drStrange.setFechaPublicacion("18/07/2024");
        drStrange.setStock(12);
        drStrange.setPrecio(6.49);
        comics.add(drStrange);

        // Fantastic Four
        Comic ff = new Comic();
        ff.setId(6);
        ff.setTitulo("Fantastic Four: First Family");
        ff.setDescripcion("Los cuatro fantásticos regresan a sus raíces en esta nueva serie familiar.");
        ff.setImagenUrl("https://i.imgur.com/FF1.jpg");
        ff.setFechaPublicacion("25/08/2024");
        ff.setStock(18);
        ff.setPrecio(4.99);
        comics.add(ff);

        // Guardians of the Galaxy
        Comic gotg = new Comic();
        gotg.setId(7);
        gotg.setTitulo("Guardians of the Galaxy: Cosmic Adventures");
        gotg.setDescripcion("Star-Lord y su equipo enfrentan amenazas cósmicas en el espacio profundo.");
        gotg.setImagenUrl("https://i.imgur.com/GOTG1.jpg");
        gotg.setFechaPublicacion("12/09/2024");
        gotg.setStock(22);
        gotg.setPrecio(5.99);
        comics.add(gotg);

        // Captain America
        Comic cap = new Comic();
        cap.setId(8);
        cap.setTitulo("Captain America: Shield of Liberty");
        cap.setDescripcion("Steve Rogers defiende la justicia y la libertad en una nueva era de héroes.");
        cap.setImagenUrl("https://i.imgur.com/Cap1.jpg");
        cap.setFechaPublicacion("30/09/2024");
        cap.setStock(16);
        cap.setPrecio(4.99);
        comics.add(cap);

        return comics;
    }

    private void updateSpinner() {
        List<String> titulos = new ArrayList<>();
        titulos.add("Seleccionar comic...");
        
        for (Comic comic : comicsDisponibles) {
            String disponibilidad = comic.getStock() > 0 ? " (Disponible: " + comic.getStock() + ")" : " (Agotado)";
            titulos.add(comic.getTitulo() + disponibilidad);
        }
        
        spinnerAdapter.clear();
        spinnerAdapter.addAll(titulos);
        spinnerAdapter.notifyDataSetChanged();
    }

    private void showComicPreview(Comic comic) {
        layoutPreview.setVisibility(View.VISIBLE);
        
        tvTituloPreview.setText(comic.getTitulo());
        tvDescripcionPreview.setText(comic.getDescripcion());
        
        // Cargar imagen
        if (comic.getImagenUrl() != null && !comic.getImagenUrl().isEmpty()) {
            Picasso.get()
                .load(comic.getImagenUrl())
                .placeholder(R.drawable.placeholder_hero)
                .error(R.drawable.error_hero)
                .into(ivPreviewComic);
        } else {
            ivPreviewComic.setImageResource(R.drawable.placeholder_hero);
        }
    }

    private void hideComicPreview() {
        layoutPreview.setVisibility(View.GONE);
    }

    private void validateAndSubmitRequest() {
        if (comicSeleccionado == null) {
            Toast.makeText(getContext(), "Por favor selecciona un comic", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidad = etCantidad.getText().toString().trim();
        String motivo = etMotivo.getText().toString().trim();
        int prioridadId = rgPrioridad.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(cantidad)) {
            etCantidad.setError("Ingresa la cantidad");
            return;
        }

        if (TextUtils.isEmpty(motivo)) {
            etMotivo.setError("Ingresa el motivo de la solicitud");
            return;
        }

        if (prioridadId == -1) {
            Toast.makeText(getContext(), "Selecciona una prioridad", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidadInt = Integer.parseInt(cantidad);
        if (cantidadInt <= 0) {
            etCantidad.setError("La cantidad debe ser mayor a 0");
            return;
        }

        if (cantidadInt > comicSeleccionado.getStock()) {
            etCantidad.setError("No hay suficiente stock disponible");
            return;
        }

        submitRequest(cantidad, motivo, prioridadId);
    }

    private void submitRequest(String cantidad, String motivo, int prioridadId) {
        RadioButton radioButton = getView().findViewById(prioridadId);
        String prioridad = radioButton.getText().toString();

        // Simular envío de solicitud
        lottieLoading.setVisibility(View.VISIBLE);
        
        new android.os.Handler().postDelayed(() -> {
            saveSolicitud(cantidad, motivo, prioridad);
            lottieLoading.setVisibility(View.GONE);
            showSuccessMessage();
            clearForm();
        }, 1500);
    }

    private void saveSolicitud(String cantidad, String motivo, String prioridad) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fecha = sdf.format(new Date());

        // Guardar en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String solicitudKey = "solicitud_" + currentUser + "_" + System.currentTimeMillis();
        
        editor.putString(solicitudKey + "_comic", comicSeleccionado.getTitulo());
        editor.putString(solicitudKey + "_cantidad", cantidad);
        editor.putString(solicitudKey + "_motivo", motivo);
        editor.putString(solicitudKey + "_prioridad", prioridad);
        editor.putString(solicitudKey + "_fecha", fecha);
        editor.apply();
    }

    private void showSuccessMessage() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("¡Solicitud Exitosa!")
            .setMessage("Tu solicitud ha sido registrada correctamente. Te notificaremos cuando esté lista.")
            .setPositiveButton("Aceptar", null)
            .setIcon(R.drawable.ic_star)
            .show();
    }

    private void clearForm() {
        spinnerComics.setSelection(0);
        etCantidad.setText("");
        etMotivo.setText("");
        rgPrioridad.clearCheck();
        hideComicPreview();
    }
} 