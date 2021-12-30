package com.jackbracey.recipe.Rest;

import com.jackbracey.recipe.Domain.Recipe;
import com.jackbracey.recipe.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    HibernateTemplate ht = new HibernateTemplate();

    @GetMapping("/get")
    private String getRecipes() {
        recipeService.getRecipe(1L);
        return "Recipe biiitch";
    }

    @PostMapping("/create")
    private boolean createRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Test");
        recipeService.createRecipe(recipe);
        return false;
    }

}
