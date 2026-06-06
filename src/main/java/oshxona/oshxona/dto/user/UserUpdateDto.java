package oshxona.oshxona.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String fullName;
    @NotBlank
    @Pattern(regexp = "^\\+998\\d{9}$")
    private String phone;
    private String roleId;
}