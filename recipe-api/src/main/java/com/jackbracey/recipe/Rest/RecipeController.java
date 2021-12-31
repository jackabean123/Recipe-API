package com.jackbracey.recipe.Rest;

import com.jackbracey.recipe.Domain.Recipe;
import com.jackbracey.recipe.Service.RecipeService;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

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

    @GetMapping("scrape")
    private String scrapeBbc() {

        StringWriter out = new StringWriter();
        PythonInterpreter interp = new PythonInterpreter();
        interp.setOut(out);
        interp.setErr(out);

        try {
            File pyFile = ResourceUtils.getFile("classpath:BBCGoodFood.py");
            if (pyFile.exists()) {
                List<String> lines = Files.readAllLines(pyFile.toPath());
                for (String line : lines) {
                    System.out.println(line);
                }
            } else {
                return "";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = out.toString();
        System.out.println("result: " + result);
        PyObject recipes = interp.get("recipes");

        return result;
    }

}
