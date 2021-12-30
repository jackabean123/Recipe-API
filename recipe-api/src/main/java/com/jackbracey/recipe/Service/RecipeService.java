package com.jackbracey.recipe.Service;

import com.jackbracey.recipe.Domain.Recipe;
import com.jackbracey.recipe.Repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.getById(id);
    }

    public void createRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

}
