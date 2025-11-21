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
@Table(name = "RECIPE_STEPS")
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_step_no")
    private Integer recipeStepNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_no", nullable = false)
    private Recipe recipe; // FK: RECIPES.recipe_no

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
}