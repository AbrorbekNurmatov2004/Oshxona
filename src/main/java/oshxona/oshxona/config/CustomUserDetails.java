package oshxona.oshxona.config;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import oshxona.oshxona.model.Permission;
import oshxona.oshxona.model.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
public class CustomUserDetails implements UserDetails {

    private String id;
    private String phone;
    private String password;
    private Role role;
    private Boolean superAdmin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (role == null) {
            return authorities;
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
        List<Permission> permissions = role.getPermissions();
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getCode())));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.phone;
    }

}
