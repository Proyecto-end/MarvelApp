package com.example.quiz2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz2.adaptador.Noticias_adaptador;
import com.example.quiz2.clases.Noticia;

import java.util.ArrayList;
import java.util.List;

public class NoticiasFragment extends Fragment {

    RecyclerView rcv_noticias;
    List<Noticia> Lista_noticias = new ArrayList<>();


    public NoticiasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragment
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        rcv_noticias = view.findViewById(R.id.rcv_noticias);

        // Asegúrate de que la lista de noticias no se duplique si el fragment se recrea
        if (Lista_noticias.isEmpty()) {
            Noticia noticia1 = new Noticia("Noticia 1", "Asesinan a un profesor jaja", "https://es.vecteezy.com/arte-vectorial/7410738-hombre-barbudo-ilustracion-en-estilo-de-dibujos-animados-planos");
            Noticia noticia2 = new Noticia("Noticia 2", "Autonoma uac", "https://es.vecteezy.com/arte-vectorial/7410738-hombre-barbudo-ilustracion-en-estilo-de-dibujos-animados-planos");
            Noticia noticia3 = new Noticia("Noticia 3", "Atlantico UA", "https://es.vecteezy.com/arte-vectorial/7410738-hombre-barbudo-ilustracion-en-estilo-de-dibujos-animados-planos");
            Noticia noticia4 = new Noticia("Noticia 4", "Cuc Umum", "https://es.vecteezy.com/arte-vectorial/7410738-hombre-barbudo-ilustracion-en-estilo-de-dibujos-animados-planos");
            Noticia noticia5 = new Noticia("Noticia 5", "Universidad del norte", "https://es.vecteezy.com/arte-vectorial/7410738-hombre-barbudo-ilustracion-en-estilo-de-dibujos-animados-planos");

            Lista_noticias.add(noticia1);
            Lista_noticias.add(noticia2);
            Lista_noticias.add(noticia3);
            Lista_noticias.add(noticia4);
            Lista_noticias.add(noticia5);
        }

        rcv_noticias.setLayoutManager(new LinearLayoutManager(getContext())); // Usar getContext()
        rcv_noticias.setAdapter(new Noticias_adaptador(Lista_noticias));

        return view;
    }
} 