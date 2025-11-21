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
@Table(name = "REVIEWS")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_no")
    private Integer reviewNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_no", nullable = false)
    private Member author; // FK: MEMBERS.member_no

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_no", nullable = false)
    private Recipe recipe; // FK: RECIPES.recipe_no

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "rating", nullable = false)
    private Float rating;

    @Column(name = "spicy_level", nullable = false)
    private Integer spicyLevel;

    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "del_date")
    private LocalDateTime delDate;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

}