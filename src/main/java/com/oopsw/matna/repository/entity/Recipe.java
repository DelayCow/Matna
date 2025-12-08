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

    // ★ 1. 후기 추가 시 평점 갱신
//    public void addRating(float newRating) {
//        float totalScore = this.averageRating * this.reviewCount; // 기존 총점
//        this.reviewCount++; // 개수 증가
//        this.averageRating = (totalScore + newRating) / this.reviewCount; // 새 평균
//    }

    // ★ 2. 후기 삭제 시 평점 갱신
    public void removeRating(float deletedRating) {
        if (this.reviewCount <= 1) {
            // 마지막 후기 삭제면 초기화
            this.reviewCount = 0;
            this.averageRating = 0.0f;
        } else {
            float totalScore = this.averageRating * this.reviewCount; // 기존 총점
            this.reviewCount--; // 개수 감소
            this.averageRating = (totalScore - deletedRating) / this.reviewCount; // 새 평균
        }
    }
}