package oshxona.oshxona.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import oshxona.oshxona.model.base.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "food")
public class Food extends BaseEntity {

    private String name;

    @Column(updatable = false)
    private String code;

    @Column(length = 600)
    private String description;

    @Column(nullable = false)
    private Double price = 0.0;

    private String image;

    private boolean active = true;
}
