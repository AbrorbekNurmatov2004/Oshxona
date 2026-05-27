package oshxona.oshxona.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    @NotEmpty
    private List<String> permissionIds;
}
