package com.jackbracey.recipe.Service;

import com.jackbracey.recipe.Domain.Recipe;
import com.jackbracey.recipe.POJOs.RecipeDataSources;
import com.jackbracey.recipe.Repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<String> GetUrls(RecipeDataSources recipeDataSources) {
        Optional<List<String>> urls = recipeRepository.findUrlByRecipeDataSource(recipeDataSources);
        return urls.map(strings -> strings.stream().map(Object::toString).collect(Collectors.toList())).orElse(null);
    }

}
