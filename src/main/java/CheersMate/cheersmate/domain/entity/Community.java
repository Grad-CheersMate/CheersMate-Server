package CheersMate.cheersmate.domain.entity;

import CheersMate.cheersmate.users.entity.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    // 작성자와의 관계: 한 명의 사용자가 여러 게시물을 작성할 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String title;

    // 이미지의 경우 실제 파일은 외부 스토리지에 저장하고 URL만 저장
    @Column(length = 1000)
    private String imageLink;

    @Column(length = 2000)
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
