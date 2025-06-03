package com.example.quiz2.adaptador;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz2.DetalleSuperheroActivity;
import com.example.quiz2.R;
import com.example.quiz2.clases.Superheroe;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import android.widget.ImageButton;
import com.google.android.material.imageview.ShapeableImageView;

public class SuperheroeAdapter extends RecyclerView.Adapter<SuperheroeAdapter.SuperheroeViewHolder> {

    private Context context;
    private List<Superheroe> superheroes;
    private OnSuperheroeClickListener listener;

    public SuperheroeAdapter(Context context, List<Superheroe> superheroes, OnSuperheroeClickListener listener) {
        this.context = context;
        this.superheroes = superheroes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuperheroeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_superheroe, parent, false);
        return new SuperheroeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuperheroeViewHolder holder, int position) {
        Superheroe superheroe = superheroes.get(position);
        holder.bind(superheroe);
    }

    @Override
    public int getItemCount() {
        return superheroes != null ? superheroes.size() : 0;
    }

    public void updateList(List<Superheroe> newList) {
        this.superheroes = newList;
        notifyDataSetChanged();
    }

    public void filter(String query, String categoria) {
        // Implementation of filter method
    }

    public class SuperheroeViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView ivSuperheroe;
        private TextView tvNombre;
        private TextView tvUniverso;
        private TextView tvDescripcion;
        private TextView tvEstado;
        private View estadoPunto;
        private TextView tvPopularidad;
        private TextView tvComics;
        private MaterialButton btnVerMas;

        public SuperheroeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSuperheroe = itemView.findViewById(R.id.heroImage);
            tvNombre = itemView.findViewById(R.id.heroName);
            tvUniverso = itemView.findViewById(R.id.tvUniverso);
            tvDescripcion = itemView.findViewById(R.id.heroDescription);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            estadoPunto = itemView.findViewById(R.id.estadoPunto);
            tvPopularidad = itemView.findViewById(R.id.tvPopularidad);
            tvComics = itemView.findViewById(R.id.tvComics);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);
        }

        public void bind(Superheroe superheroe) {
            // Imagen circular
            Glide.with(context)
                .load(superheroe.getImagenUrl())
                .placeholder(R.drawable.marvel_logo)
                .error(R.drawable.marvel_logo)
                .into(ivSuperheroe);

            // Nombre y universo
            tvNombre.setText(superheroe.getNombre());
            tvUniverso.setText(superheroe.getUniverso());

            // Descripción recortada
            if (superheroe.getDescripcion() != null && !superheroe.getDescripcion().isEmpty()) {
                String desc = superheroe.getDescripcion();
                if (desc.length() > 40) {
                    desc = desc.substring(0, 40) + "...";
                }
                tvDescripcion.setText(desc);
                tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                tvDescripcion.setVisibility(View.GONE);
            }

            // Número de cómics
            if (superheroe.getComics() != null && !superheroe.getComics().isEmpty()) {
                tvComics.setText(superheroe.getComics().size() + " cómics");
                tvComics.setVisibility(View.VISIBLE);
            } else {
                tvComics.setText("0 cómics");
                tvComics.setVisibility(View.VISIBLE);
            }

            // Estado y punto de color
            tvEstado.setVisibility(View.GONE);
            estadoPunto.setVisibility(View.GONE);

            // Popularidad
            tvPopularidad.setText(superheroe.getPopularidad() + "%");

            // Botón Ver Más
            btnVerMas.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerMasClick(superheroe);
                }
            });
        }
    }

    public interface OnSuperheroeClickListener {
        void onSuperheroeClick(Superheroe superheroe);
        void onFavoritoClick(Superheroe superheroe);
        void onVerMasClick(Superheroe superheroe);
    }
} 