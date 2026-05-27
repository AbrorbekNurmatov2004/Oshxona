package oshxona.oshxona.model;

import jakarta.persistence.*;
import lombok.*;
import oshxona.oshxona.model.base.BaseEntity;
import oshxona.oshxona.model.enums.OrderStatus;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    private Integer totalAmount = 0;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String acceptedBy;
    private String canceledBy;

    private String receipt;

    private String comments;
}
