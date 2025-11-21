package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "RECIPE_ALTERNATIVE_INGREDIENTS")
public class RecipeAlternativeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alternative_ingredient_no")
    private Integer alternativeIngredientNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_no", nullable = false)
    private Reviews review; // FK: REVIEWS.review_no

    @Column(name = "original_ingredient_name", nullable = false)
    private String originalIngredientName;

    @Column(name = "alternative_ingredient_name", nullable = false)
    private String alternativeIngredientName;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;
}