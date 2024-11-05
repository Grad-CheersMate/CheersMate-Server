package CheersMate.cheersmate.domain.dto;

public class LiquorDTO {

    private Long id;
    private String name;
    private Double alcohol;
    private String imageLink;
    private String category;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(Double alcohol) {
        this.alcohol = alcohol;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}