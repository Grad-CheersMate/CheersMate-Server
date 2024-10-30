package CheersMate.cheersmate.food.entity;

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
    private String description;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodcateId")
    private FoodCategory category;
}
