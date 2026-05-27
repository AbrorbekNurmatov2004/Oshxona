package oshxona.oshxona.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import oshxona.oshxona.model.base.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "chat_id", nullable = false, unique = true)
    private String chatId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;
}
