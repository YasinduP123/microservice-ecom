package edu.yasidu.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ORDER_ITEM")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name =  "FK_PRODUCT_ID")
    private Long productId;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "FK_INVENTORY_ID")
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name = "FK_ORDER")
    private Order fkOrder;
}
