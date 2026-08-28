package edu.yasidu.order_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Column(name = "ITEM_COUNT")
    private Integer itemCount;

    @OneToMany(mappedBy = "fkOrder",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
