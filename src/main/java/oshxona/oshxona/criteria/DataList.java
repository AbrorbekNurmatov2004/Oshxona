package oshxona.oshxona.criteria;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataList<T> {
    private T data;
    private Long allElements;
    private Integer totalPages;
}
