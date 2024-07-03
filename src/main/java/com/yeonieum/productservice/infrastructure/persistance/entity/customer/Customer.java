package com.yeonieum.productservice.infrastructure.persistance.entity.customer;

import com.yeonieum.productservice.infrastructure.persistance.commons.converter.ActiveStatusConverter;
import com.yeonieum.productservice.infrastructure.persistance.commons.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_password", nullable = false)
    private String customerPassword;

    @Column(name = "customer_birthday", nullable = false)
    private LocalDate customerBirthday;

    @Column(name = "customer_phone_number", nullable = false)
    private String customerPhoneNumber;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_business_number", nullable = false)
    private String storeBusinessNumber;

    @Column(name = "store_address", nullable = false)
    private String storeAddress;

    @Column(name = "store_phone_number", nullable = false)
    private String storePhoneNumber;

    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private ActiveStatus isDeleted = ActiveStatus.INACTIVE;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomerMember> customerMemberList = new ArrayList<>();

}
