package oshxona.oshxona.model;

import jakarta.persistence.*;
import lombok.*;
import oshxona.oshxona.model.base.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private String fullName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private Boolean superAdmin = false;

    @Column(unique = true,nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    private String profileImage;
}
