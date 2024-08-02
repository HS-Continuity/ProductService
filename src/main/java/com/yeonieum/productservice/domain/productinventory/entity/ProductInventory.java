package com.yeonieum.productservice.domain.productinventory.entity;

import com.yeonieum.productservice.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_inventory")
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_inventory_id")
    private Long productInventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "warehousing_date", nullable = false)
    private LocalDate warehouseDate;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }
}
