package oshxona.oshxona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import oshxona.oshxona.model.Permission;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    @Query("""
            select p from Permission p where
            (:search is null
                        or lower(p.name) ilike lower(concat('%',:search,'%'))
                        or lower(p.code) ilike lower(concat('%',:search,'%')))
            """)
    Page<Permission> findAllByCriteria(String search, Pageable pageable);

    List<Permission> findAllByIdIn(List<String> permissionIds);

}
