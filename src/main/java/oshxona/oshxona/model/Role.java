package oshxona.oshxona.model;

import jakarta.persistence.*;
import lombok.*;
import oshxona.oshxona.model.base.IdEntity;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends IdEntity {

    private String name;

    @Column(nullable = false,unique = true)
    private String code;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}