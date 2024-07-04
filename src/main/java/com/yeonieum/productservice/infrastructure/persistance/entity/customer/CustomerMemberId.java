package com.yeonieum.productservice.infrastructure.persistance.entity.customer;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerMemberId implements Serializable {
    private Long customerId;
    private String memberId;
}
