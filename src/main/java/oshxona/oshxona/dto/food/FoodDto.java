package oshxona.oshxona.dto.food;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    private String id;
    private String name;
    private String code;
    private String description;
    private double price;
    private String image;
    private Integer totalAmount;
    private boolean active;
}
