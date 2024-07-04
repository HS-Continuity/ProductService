package com.yeonieum.productservice.infrastructure.persistance.entity.customer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "customer_member")
public class CustomerMember {

    @EmbeddedId
    private CustomerMemberId customerMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
