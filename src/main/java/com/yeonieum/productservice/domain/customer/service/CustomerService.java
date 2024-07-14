package com.yeonieum.productservice.domain.customer.service;

import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 고객 목록 조회
     * @param pageable 페이징 정보
     * @return 고객의 정보
     */
    @Transactional
    public Page<CustomerResponse.RetrieveCustomerDto> retrieveCustomers(Pageable pageable) {

        Page<Customer> customers = customerRepository.findByIsDeleted(pageable);

        return customers.map(customer -> CustomerResponse.RetrieveCustomerDto.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerBirthday(customer.getCustomerBirthday())
                .customerPhoneNumber(customer.getCustomerPhoneNumber())
                .storeName(customer.getStoreName())
                .build());
    }

    /**
     * 고객 상세정보 조회
     * @param customerId 페이징 정보
     * @return 고객의 상세 정보
     */
    @Transactional
    public CustomerResponse.RetrieveDetailCustomerDto retrieveDetailCustomers(Long customerId) {

        Customer targetCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 ID 입니다."));

        return CustomerResponse.RetrieveDetailCustomerDto.builder()
                .customerId(targetCustomer.getCustomerId())
                .customerName(targetCustomer.getCustomerName())
                .customerBirthday(targetCustomer.getCustomerBirthday())
                .customerPhoneNumber(targetCustomer.getCustomerPhoneNumber())
                .storeName(targetCustomer.getStoreName())
                .storeBusinessNumber(targetCustomer.getStoreBusinessNumber())
                .storeImage(targetCustomer.getStoreImage())
                .storeAddress(targetCustomer.getStoreAddress())
                .storePhoneNumber(targetCustomer.getStorePhoneNumber())
                .build();
    }
}
