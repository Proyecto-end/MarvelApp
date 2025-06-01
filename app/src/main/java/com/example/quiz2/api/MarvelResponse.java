package com.example.quiz2.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarvelResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("results")
        private List<Character> results;

        public List<Character> getResults() {
            return results;
        }
    }

    public static class Character {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("thumbnail")
        private Thumbnail thumbnail;

        @SerializedName("comics")
        private ComicList comics;

        @SerializedName("universe")
        private String universe;

        @SerializedName("active")
        private boolean active;

        @SerializedName("popularity")
        private int popularity;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public ComicList getComics() {
            return comics;
        }

        public String getUniverse() {
            return universe != null ? universe : "Marvel 616";
        }

        public boolean isActive() {
            return active;
        }

        public int getPopularity() {
            return popularity;
        }
    }

    public static class ComicList {
        @SerializedName("items")
        private List<ComicSummary> items;

        public List<ComicSummary> getItems() {
            return items;
        }
    }

    public static class ComicSummary {
        @SerializedName("resourceURI")
        private String resourceURI;

        @SerializedName("name")
        private String name;

        public String getResourceURI() {
            return resourceURI;
        }

        public String getName() {
            return name;
        }
    }

    public static class Comic {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("thumbnail")
        private Thumbnail thumbnail;

        @SerializedName("prices")
        private List<ComicPrice> prices;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
        }

        public List<ComicPrice> getPrices() {
            return prices;
        }

        public void setPrices(List<ComicPrice> prices) {
            this.prices = prices;
        }
    }

    public static class ComicPrice {
        @SerializedName("type")
        private String type;

        @SerializedName("price")
        private float price;

        public String getType() {
            return type;
        }

        public float getPrice() {
            return price;
        }
    }

    public static class Thumbnail {
        @SerializedName("path")
        private String path;

        @SerializedName("extension")
        private String extension;

        public String getFullPath() {
            return path + "." + extension;
        }
    }
} 