package oshxona.oshxona.dto.food;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodCreateDto {
    @NotBlank(message = "Taom nomi kiritilishi shart")
    private String name;
    private String description;
    @NotNull(message = "Narx kiritilishi shart")
    @PositiveOrZero(message = "Narx 0 yoki undan katta bo'lishi kerak")
    private Double price;
    private MultipartFile image;
    @Min(value = 0, message = "Soni 0 dan kam bo'lishi mumkin emas")
    private Integer totalAmount;
}
