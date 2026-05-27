    package oshxona.oshxona.dto.permission;

    import lombok.*;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PermissionDto {
        private String id;
        private String name;
        private String code;
    }
