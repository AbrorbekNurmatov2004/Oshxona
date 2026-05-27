package oshxona.oshxona.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionCreateDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;
    @NotBlank
    @Pattern(regexp = "^[a-z:]+$",
            message = "Code must be in lower and can only contain colon")
    private String code;
}
