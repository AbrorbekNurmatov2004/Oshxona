package oshxona.oshxona.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import oshxona.oshxona.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,String> {

    @Query("select o from Order o where o.deleted = false and " +
            "(lower(o.client.name) like lower(concat('%', :search, '%')) or " +
            " lower(o.receipt) like lower(concat('%', :search, '%'))) " +
            "order by o.createdAt desc")
    Page<Order> findAllOrders(Pageable pageable, String search);

    List<Order> findFirst5ByClientChatIdOrderByCreatedAtDesc(String chatId);

}
