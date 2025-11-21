package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RECIPES")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_no")
    private Integer recipeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_no", nullable = false)
    private Member author; // FK: MEMBERS.member_no

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "prep_time", nullable = false)
    private Integer prepTime;

    @Column(name = "servings", nullable = false)
    private Integer servings;

    @Column(name = "spicy_level", nullable = false)
    private Integer spicyLevel;

    @Column(name = "difficulty", nullable = false, length = 10)
    private String difficulty;

    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;

    @Column(name = "del_date")
    private LocalDateTime delDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount;

    @Column(name = "average_rating", nullable = false)
    private Float averageRating;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;
}