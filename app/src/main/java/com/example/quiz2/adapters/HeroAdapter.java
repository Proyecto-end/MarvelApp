package com.example.quiz2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz2.R;
import com.example.quiz2.api.MarvelResponse;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {
    private List<MarvelResponse.Character> heroes;
    private OnHeroClickListener listener;

    public interface OnHeroClickListener {
        void onHeroClick(MarvelResponse.Character hero);
    }

    public HeroAdapter(OnHeroClickListener listener) {
        this.heroes = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_hero, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        MarvelResponse.Character hero = heroes.get(position);
        holder.bind(hero);
    }

    @Override
    public int getItemCount() {
        return heroes.size();
    }

    public void updateHeroes(List<MarvelResponse.Character> newHeroes) {
        this.heroes = newHeroes;
        notifyDataSetChanged();
    }

    public void filterHeroes(String query) {
        List<MarvelResponse.Character> filteredList = new ArrayList<>();
        for (MarvelResponse.Character hero : heroes) {
            if (hero.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(hero);
            }
        }
        updateHeroes(filteredList);
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {
        private ImageView heroImage;
        private TextView heroName;
        private TextView heroDescription;

        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            heroImage = itemView.findViewById(R.id.heroImage);
            heroName = itemView.findViewById(R.id.heroName);
            heroDescription = itemView.findViewById(R.id.heroDescription);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onHeroClick(heroes.get(position));
                }
            });
        }

        void bind(MarvelResponse.Character hero) {
            heroName.setText(hero.getName());
            heroDescription.setText(hero.getDescription());

            if (hero.getThumbnail() != null) {
                String imageUrl = hero.getThumbnail().getFullPath();
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_hero)
                    .error(R.drawable.error_hero)
                    .into(heroImage);
            }
        }
    }
} 