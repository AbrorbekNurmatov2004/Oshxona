package oshxona.oshxona.criteria;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseCriteria {
    private String search;
    private Integer page;
    private Integer size;
}
