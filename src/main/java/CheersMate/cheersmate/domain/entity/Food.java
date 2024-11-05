package CheersMate.cheersmate.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Food")
@Data
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    private String name;
    private String image;
    private String category;
}
