package oshxona.oshxona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import oshxona.oshxona.model.Food;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, String> {

    @Query("""
            select d from Food d
            where d.deleted = false
            and (:search is null
                or lower(d.name) like lower(concat('%', :search, '%'))
                or lower(d.code) like lower(concat('%', :search, '%')))
            order by d.createdAt desc
            """)
    Page<Food> findAllFoods(Pageable pageable, String search);

    @Query(value = "select count(*) from food", nativeQuery = true)
    long countAllFoods();

    Optional<Food> findByCode(String code);

    List<Food> findAllByActiveTrue();

}
