package com.example.quiz2.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz2.R;
import com.example.quiz2.clases.Noticia;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Noticias_adaptador extends RecyclerView.Adapter<Noticias_adaptador.ViewHolder> {
    private List<Noticia> datos;
    public Noticias_adaptador(List<Noticia> datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public Noticias_adaptador.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_noticias, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Noticias_adaptador.ViewHolder holder, int position) {
        Noticia dato = datos.get(position);
        holder.bind(dato);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_nombre, txt_info;
        ImageView img_noticia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nombre = itemView.findViewById(R.id.txt_nombre);
            txt_info = itemView.findViewById(R.id.txt_info);
            img_noticia = itemView.findViewById(R.id.img_noticia);
        }

        public void bind(Noticia dato) {
            txt_nombre.setText(dato.getNombre());
            txt_info.setText(dato.getInfo());
            Picasso.get().load(dato.getImagen()).into(img_noticia);
        }
    }
}