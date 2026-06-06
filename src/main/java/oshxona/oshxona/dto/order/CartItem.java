package oshxona.oshxona.dto.order;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private int count;
    private String comment = "";
}
