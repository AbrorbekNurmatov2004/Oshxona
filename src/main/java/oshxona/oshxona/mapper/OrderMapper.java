package oshxona.oshxona.mapper;

import org.springframework.stereotype.Component;
import oshxona.oshxona.dto.IdNameDTO;
import oshxona.oshxona.dto.order.OrderDto;
import oshxona.oshxona.model.Client;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.model.Order;
import oshxona.oshxona.model.User;
import oshxona.oshxona.validator.UserValidator;

import java.util.List;

@Component
public class OrderMapper {

    private final UserValidator userValidator;

    public OrderMapper(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    public List<OrderDto> toDto(List<Order> orders) {
        return orders.stream().map(this::toDto).toList();
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .client(toIdNameDto(order.getClient()))
                .food(toIdNameDto(order.getFood()))
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .acceptedBy(toIdNameDtoFromUser(order.getAcceptedBy()))
                .canceledBy(toIdNameDtoFromUser(order.getCanceledBy()))
                .receipt(order.getReceipt())
                .comments(order.getComments())
                .build();
    }

    private IdNameDTO toIdNameDto(Client client) {
        if (client == null) return null;
        return IdNameDTO.builder().
                id(client.getId()).
                name(client.getName()).
                build();
    }

    private IdNameDTO toIdNameDto(Food food) {
        if (food == null) return null;
        return IdNameDTO.builder().
                id(food.getId()).
                name(food.getName()).
                build();
    }

    private IdNameDTO toIdNameDtoFromUser(String userId) {
        if (userId == null) return null;
        User user = userValidator.existAndGet(userId);
        return IdNameDTO.builder().
                id(user.getId()).
                name(user.getFullName()).
                build();
    }
}
