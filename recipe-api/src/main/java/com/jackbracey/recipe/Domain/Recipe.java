package com.jackbracey.recipe.Domain;

import com.jackbracey.recipe.Domain.Ingredient.IngredientToRecipe;
import com.jackbracey.recipe.POJOs.RecipeDataSources;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_data_source", nullable = false)
    private RecipeDataSources recipeDataSource;

    @OneToMany(mappedBy = "recipe")
    private List<IngredientToRecipe> ingredientToRecipe = new ArrayList<>();

    public List<IngredientToRecipe> getIngredientToRecipe() {
        return ingredientToRecipe;
    }

    public void setIngredientToRecipe(List<IngredientToRecipe> ingredientToRecipe) {
        this.ingredientToRecipe = ingredientToRecipe;
    }


    public Recipe(String name, String url, RecipeDataSources recipeDataSource) {
        this.name = name;
        this.url = url;
        this.recipeDataSource = recipeDataSource;
    }

    public Recipe() {
    }

    public RecipeDataSources getRecipeDataSource() {
        return recipeDataSource;
    }

    public void setRecipeDataSource(RecipeDataSources recipeDataSource) {
        this.recipeDataSource = recipeDataSource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
}