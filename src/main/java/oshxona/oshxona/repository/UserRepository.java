package oshxona.oshxona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import oshxona.oshxona.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("""
            select d from User d
            where d.deleted = false
            and (:search is null
                or lower(d.fullName) like lower(concat('%', :search, '%'))
                or lower(d.phone) like lower(concat('%', :search, '%')))
            order by d.createdAt desc
            """)
    Page<User> findAllUsers(Pageable pageable, String search);

    Optional<User> findByPhoneAndDeletedFalse(String phone);

    boolean existsByPhoneAndDeletedFalse(String phone);

    boolean existsByPhoneAndIdNotAndDeletedFalse(String phone, String id);

}
