package com.yeonieum.productservice.domain.customer.dto;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class CustomerResponse {

    @Getter
    @Builder
    public static class OfRetrieveCustomer {

        private Long customerId;
        private String customerName;
        private LocalDate customerBirthday;
        private String customerPhoneNumber;
        private String storeName;
        private String storeBusinessNumber;

        public static CustomerResponse.OfRetrieveCustomer convertedBy(Customer customer) {
            return CustomerResponse.OfRetrieveCustomer.builder()
                    .customerId(customer.getCustomerId())
                    .customerName(customer.getCustomerName())
                    .customerBirthday(customer.getCustomerBirthday())
                    .customerPhoneNumber(customer.getCustomerPhoneNumber())
                    .storeName(customer.getStoreName())
                    .storeBusinessNumber(customer.getStoreBusinessNumber())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OfRetrieveDetailCustomer {

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

        public static CustomerResponse.OfRetrieveDetailCustomer convertedBy(Customer customer) {
            return OfRetrieveDetailCustomer.builder()
                    .customerId(customer.getCustomerId())
                    .customerName(customer.getCustomerName())
                    .customerBirthday(customer.getCustomerBirthday())
                    .customerPhoneNumber(customer.getCustomerPhoneNumber())
                    .storeName(customer.getStoreName())
                    .storeBusinessNumber(customer.getStoreBusinessNumber())
                    .storeImage(customer.getStoreImage())
                    .storeAddress(customer.getStoreAddress())
                    .storePhoneNumber(customer.getStorePhoneNumber())
                    .deliveryFee(customer.getDeliveryFee())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OfRetrieveForAuth {
        String storeBusinessNumber;
        Long customerId;
        String password;

        public static OfRetrieveForAuth convertedBy(Customer customer) {
            return OfRetrieveForAuth.builder()
                    .storeBusinessNumber(customer.getStoreBusinessNumber())
                    .customerId(customer.getCustomerId())
                    .password(customer.getCustomerPassword())
                    .build();
        }
    }
}
