package com.example.quiz2.adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz2.DetalleSuperheroActivity;
import com.example.quiz2.R;
import com.example.quiz2.clases.Superheroe;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class SuperheroeAdapter extends RecyclerView.Adapter<SuperheroeAdapter.SuperheroeViewHolder> {

    private Context context;
    private List<Superheroe> superheroesList;
    private List<Superheroe> superheroesListFiltered;
    private OnSuperheroeFavoriteListener favoriteListener;

    public interface OnSuperheroeFavoriteListener {
        void onFavoriteClick(Superheroe superheroe, boolean isFavorite);
    }

    public SuperheroeAdapter(Context context, List<Superheroe> superheroesList) {
        this.context = context;
        this.superheroesList = superheroesList;
        this.superheroesListFiltered = new ArrayList<>(superheroesList);
    }

    public void setOnSuperheroeFavoriteListener(OnSuperheroeFavoriteListener listener) {
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public SuperheroeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_superheroe, parent, false);
        return new SuperheroeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuperheroeViewHolder holder, int position) {
        Superheroe superheroe = superheroesListFiltered.get(position);
        holder.bind(superheroe);
    }

    @Override
    public int getItemCount() {
        return superheroesListFiltered.size();
    }

    public void updateList(List<Superheroe> newList) {
        this.superheroesList = newList;
        this.superheroesListFiltered = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query, String categoria) {
        superheroesListFiltered.clear();
        
        if (query.isEmpty() && categoria.equals("Todos")) {
            superheroesListFiltered.addAll(superheroesList);
        } else {
            for (Superheroe superheroe : superheroesList) {
                boolean matchesQuery = query.isEmpty() || 
                    superheroe.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                    superheroe.getDescripcion().toLowerCase().contains(query.toLowerCase());
                
                boolean matchesCategory = categoria.equals("Todos") || 
                    (superheroe.getGrupos() != null && superheroe.getGrupos().contains(categoria));
                
                if (matchesQuery && matchesCategory) {
                    superheroesListFiltered.add(superheroe);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class SuperheroeViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView ivFotoSuperheroe;
        private TextView tvNombreSuperheroe;
        private TextView tvUniversoSuperheroe;
        private TextView tvDescripcionSuperheroe;
        private View tvEstadoSuperheroe;
        private ProgressBar pbPopularidad;
        private TextView tvPopularidad;
        private TextView tvCantidadComics;
        private Button btnVerMas;
        private ImageView btnFavorito;

        public SuperheroeViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivFotoSuperheroe = itemView.findViewById(R.id.ivSuperheroe);
            tvNombreSuperheroe = itemView.findViewById(R.id.tvNombre);
            tvUniversoSuperheroe = itemView.findViewById(R.id.tvUniverso);
            tvDescripcionSuperheroe = itemView.findViewById(R.id.tvDescripcion);
            tvEstadoSuperheroe = itemView.findViewById(R.id.viewEstado);
            pbPopularidad = itemView.findViewById(R.id.pb_popularidad);
            tvPopularidad = itemView.findViewById(R.id.tvPopularidad);
            tvCantidadComics = itemView.findViewById(R.id.tvComicsCount);
            btnVerMas = itemView.findViewById(R.id.btnVerMas);
            btnFavorito = itemView.findViewById(R.id.ivFavorite);
        }

        public void bind(Superheroe superheroe) {
            // Configurar datos básicos
            tvNombreSuperheroe.setText(superheroe.getNombre());
            tvUniversoSuperheroe.setText(superheroe.getUniverso());
            tvDescripcionSuperheroe.setText(superheroe.getDescripcion());
            
            // Configurar estado (indicador visual)
            String estado = superheroe.getEstado();
            tvEstadoSuperheroe.setBackgroundTintList(context.getColorStateList(
                estado.equals("Vivo") ? R.color.verde_activo : R.color.rojo_inactivo));
            
            // Configurar popularidad
            int popularidad = superheroe.getPopularidad();
            pbPopularidad.setProgress(popularidad);
            tvPopularidad.setText(popularidad + "%");
            
            // Configurar cantidad de comics
            tvCantidadComics.setText(superheroe.getComics().size() + " comics");
            
            // Configurar imagen
            if (superheroe.getImagenUrl() != null && !superheroe.getImagenUrl().isEmpty()) {
                Picasso.get()
                    .load(superheroe.getImagenUrl())
                    .placeholder(R.drawable.placeholder_hero)
                    .error(R.drawable.error_hero)
                    .into(ivFotoSuperheroe);
            } else {
                ivFotoSuperheroe.setImageResource(R.drawable.placeholder_hero);
            }
            
            // Configurar botón favorito
            btnFavorito.setImageResource(superheroe.isFavorito() ? 
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            
            btnFavorito.setOnClickListener(v -> {
                superheroe.setFavorito(!superheroe.isFavorito());
                btnFavorito.setImageResource(superheroe.isFavorito() ? 
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
                
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(superheroe, superheroe.isFavorito());
                }
            });
            
            // Configurar botón ver más
            btnVerMas.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetalleSuperheroActivity.class);
                intent.putExtra("superheroe_objeto", superheroe);
                context.startActivity(intent);
            });
            
            // Agregar animación al hacer clic en el item
            itemView.setOnClickListener(v -> {
                // Animación de click
                v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100);
                    });
                
                // Ir al detalle
                btnVerMas.performClick();
            });
        }
    }
} 