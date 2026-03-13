package com.fisa.service.domain.user.entity;

import com.fisa.service.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 인가 서버에서 넘어오는 고유 식별자
    @Column(name = "auth_server_user_id", length = 36, nullable = false, unique = true)
    private String authServerUserId;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 양방향 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public User(String authServerUserId, String email, String nickname) {
        this.authServerUserId = authServerUserId;
        this.email = email;
        this.nickname = nickname;
    }

    public void updateProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}