package edu.yasidu.orderService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_ID",  unique = true, nullable = false)
    private UUID orderId;

    @Column(name = "ITEM_COUNT")
    private Integer itemCount;

    @OneToMany(mappedBy = "fkOrder",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
