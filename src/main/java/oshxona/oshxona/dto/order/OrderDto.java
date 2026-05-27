package oshxona.oshxona.dto.order;

import lombok.*;
import oshxona.oshxona.dto.IdNameDTO;
import oshxona.oshxona.model.enums.OrderStatus;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String id;
    private IdNameDTO client;
    private IdNameDTO food;
    private Integer totalAmount;
    private OrderStatus orderStatus;
    private IdNameDTO acceptedBy;
    private IdNameDTO canceledBy;
    private String receipt;
    private String comments;
}
