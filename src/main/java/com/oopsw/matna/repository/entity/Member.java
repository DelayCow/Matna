package com.oopsw.matna.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "MEMBERS")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private Integer memberNo;
    @Column(name = "member_id", nullable = false, length = 30)
    private String memberId;
    @Column(name = "account_name", length = 30)
    private String accountName;
    @ToString.Exclude
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;
    @Column(name = "bank", length = 30)
    private String bank;
    @Column(name = "account_number", length = 30)
    private String accountNumber;
    @CreationTimestamp
    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;
    @Column(name = "del_date")
    private LocalDateTime delDate;
    @Column(name = "roll", nullable = false, length = 10)
    private String roll;
    @Column(name = "ban_date")
    private LocalDateTime banDate;
    @Column(name = "point", nullable = false)
    private Integer point;
    @Column(name = "image_url", length = 1000)
    private String imageUrl;
    @Column(name = "address", length = 100)
    private String address;
}

