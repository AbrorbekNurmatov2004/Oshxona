package oshxona.oshxona.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.order.CartItem;
import oshxona.oshxona.dto.order.OrderDto;
import oshxona.oshxona.mapper.OrderMapper;
import oshxona.oshxona.model.Client;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.model.Order;
import oshxona.oshxona.model.enums.OrderStatus;
import oshxona.oshxona.repository.OrderRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.utils.SecurityUtils;
import oshxona.oshxona.validator.FoodValidator;
import oshxona.oshxona.validator.OrderValidator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OrderService extends AbstractService<OrderRepository, OrderMapper, OrderValidator> {

    private final Map<String, Map<String, CartItem>> map = new ConcurrentHashMap<>();
    private final Map<String, String> comment = new ConcurrentHashMap<>();

    private final ClientService clientService;
    private final FoodValidator foodValidator;

    public OrderService(OrderRepository repository, OrderMapper mapper, OrderValidator validator, ClientService clientService, FoodValidator foodValidator) {
        super(repository, mapper, validator);
        this.clientService = clientService;
        this.foodValidator = foodValidator;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoClearCards() {
        log.info("Cron job is work");
        map.clear();
    }

    public DataList<List<OrderDto>> getAll(BaseCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Order> orders = repository.findAllOrders(pageable, criteria.getSearch());
        List<OrderDto> dto = mapper.toDto(orders.getContent());
        return DataList.<List<OrderDto>>builder()
                .data(dto)
                .totalPages(orders.getTotalPages())
                .allElements(orders.getTotalElements())
                .build();
    }

    @Transactional
    public void processOrderAction(String id, String action) {
        Order order = validator.existAndGet(id);
        String currentUser = SecurityUtils.sessionUser().getId();
        switch (action.toLowerCase()) {
            case "accept" -> {
                order.setOrderStatus(OrderStatus.ACCEPTED);
                order.setAcceptedBy(currentUser);
            }
            case "cancel" -> {
                order.setOrderStatus(OrderStatus.CANCELED);
                order.setCanceledBy(currentUser);
            }
            case "done" -> {
                order.setOrderStatus(OrderStatus.DONE);
            }
            case "delete" -> {
                repository.delete(order);
                order.setDeleted(true);
                return;
            }
        }
        repository.save(order);
    }

    public void calculation(String chatId, String foodCode, int number) {
        Map<String, CartItem> cartMemory = map.computeIfAbsent(chatId, c -> new ConcurrentHashMap<>());
        CartItem item = cartMemory.computeIfAbsent(foodCode, f -> new CartItem());

        int newCount = item.getCount() + number;
        if (newCount > 0) {
            item.setCount(newCount);
        } else {
            cartMemory.remove(foodCode);
        }
    }

    public int getFoodCount(String chatId, String code) {
        Map<String, CartItem> cart = map.get(chatId);
        if (cart == null) {
            return 0;
        }
        return cart.get(code).getCount();
    }

    public void addCommentToCart(String chatId, String foodCode, String comment) {
        Map<String, CartItem> cartMemory = map.computeIfAbsent(chatId, c -> new ConcurrentHashMap<>());
        CartItem item = cartMemory.computeIfAbsent(foodCode, f -> new CartItem());
        item.setComment(comment);
    }

    public void setActiveCommentSession(String chatId, String foodCode) {
        comment.put(chatId, foodCode);
    }

    public void removeActiveCommentSession(String chatId) {
        comment.remove(chatId);
    }

    public String getActiveCommentFoodCode(String chatId) {
        return comment.get(chatId);
    }

    public void saveOrder(String chatId) {
        Client client = clientService.findByChatId(chatId);
        Map<String, CartItem> cart = map.get(chatId);

        if (cart == null || cart.isEmpty()) {
            return;
        }

        for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
            Food food = foodValidator.getFoodCode(entry.getKey());
            CartItem cartItem = entry.getValue();

            Order order = new Order();
            order.setFood(food);
            order.setClient(client);
            order.setTotalAmount(cartItem.getCount());
            order.setOrderStatus(OrderStatus.CREATED);
            order.setReceipt("REC-" + System.currentTimeMillis() / 1000);
            order.setComments(cartItem.getComment());
            repository.save(order);
        }
        clearCart(chatId);
    }

    public List<Order> getOrders(String chatId) {
        if (chatId == null) {
            return Collections.emptyList();
        }
        return repository.findFirst5ByClientChatIdOrderByCreatedAtDesc(chatId);
    }

    public Map<String, CartItem> getCart(String chatId) {
        if (chatId == null) {
            return new ConcurrentHashMap<>();
        }
        return map.computeIfAbsent(chatId, c -> new ConcurrentHashMap<>());
    }

    public void clearCart(String chatId) {
        map.remove(chatId);
    }
}
