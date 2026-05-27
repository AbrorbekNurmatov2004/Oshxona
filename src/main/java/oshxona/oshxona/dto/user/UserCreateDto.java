package oshxona.oshxona.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class UserCreateDto {
    @NotBlank
    @Size(min = 2, max = 50)
    private String fullName;
    @NotBlank
    @Pattern(regexp = "^\\+998\\d{9}$")
    private String phone;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,64}$",
            message = "Password must be 8-64 chars and include upper, lower, number, and special character")
    private String password;
    @NotEmpty
    private String roleId;
}
