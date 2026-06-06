package oshxona.oshxona.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.order.OrderDto;
import oshxona.oshxona.service.OrderService;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('order:read')")
    @GetMapping
    public String getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            Model model
    ) {
        BaseCriteria criteria = BaseCriteria.builder()
                .search(search)
                .page(page)
                .size(size)
                .build();
        DataList<List<OrderDto>> orders = service.getAll(criteria);

        model.addAttribute("orders", orders.getData());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "order-list";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAnyAuthority('order:update', 'order:delete')")
    @PostMapping("/action")
    public String handleOrderAction(@RequestParam("id") String id, @RequestParam("action") String action) {
        service.processOrderAction(id, action);
        return "redirect:/orders";
    }
}