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
@Table(name = "permissions")
public class Permission extends IdEntity {

    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
    private List<Role> roles;
}