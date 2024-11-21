package CheersMate.cheersmate.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {
    private Long id;
    private String name;
    private Double volume;
    private String imageUrl;
    private String type;
}
