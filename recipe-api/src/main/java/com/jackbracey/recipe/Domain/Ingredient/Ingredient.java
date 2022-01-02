package com.jackbracey.recipe.Domain.Ingredient;

import javax.persistence.*;

@Entity
@Table(name = "ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(mappedBy = "ingredient", optional = false, orphanRemoval = true)
    private IngredientToRecipe ingredientToRecipe;

    public IngredientToRecipe getIngredientToRecipe() {
        return ingredientToRecipe;
    }

    public void setIngredientToRecipe(IngredientToRecipe ingredientToRecipe) {
        this.ingredientToRecipe = ingredientToRecipe;
    }

    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient() {
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

    public void setId(Long id) {
        this.id = id;
    }
}