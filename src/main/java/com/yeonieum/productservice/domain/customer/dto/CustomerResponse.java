package com.yeonieum.productservice.domain.customer.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class CustomerResponse {

    @Getter
    @Builder
    public static class RetrieveCustomerDto{

        private Long customerId;
        private String customerName;
        private LocalDate customerBirthday;
        private String customerPhoneNumber;
        private String storeName;
    }

    @Getter
    @Builder
    public static class RetrieveDetailCustomerDto{

        private Long customerId;
        private String customerName;
        private LocalDate customerBirthday;
        private String customerPhoneNumber;
        private String storeName;
        private String storeBusinessNumber;
        private String storeImage;
        private String storeAddress;
        private String storePhoneNumber;
        private int deliveryFee;
    }
}
