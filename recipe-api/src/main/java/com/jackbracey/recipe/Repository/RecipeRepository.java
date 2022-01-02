package com.jackbracey.recipe.Repository;

import com.jackbracey.recipe.Domain.Recipe;
import com.jackbracey.recipe.POJOs.RecipeDataSources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<List<String>> findUrlByRecipeDataSource(RecipeDataSources recipeDataSources);

}