package com.jackbracey.recipe.POJOs;

public enum RecipeDataSources {
    BBCGoodFood("https://www.bbcgoodfood.com");

    private String baseUrl;

    RecipeDataSources(String url) {
        this.baseUrl = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
