package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RECIPE_INGREDIENTS")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_no")
    private Integer recipeIngredientNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_no", nullable = false)
    private Recipe recipe; // FK: RECIPES.recipe_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_no", nullable = false)
    private Ingredient ingredient; // FK: INGREDIENTS.ingredient_no

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

}