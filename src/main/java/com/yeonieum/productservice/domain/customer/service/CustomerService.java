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
    public Page<CustomerResponse.OfRetrieveCustomer> retrieveCustomers(Pageable pageable) {

        Page<Customer> customers = customerRepository.findByIsDeleted(pageable);

        return customers.map(customer -> CustomerResponse.OfRetrieveCustomer.convertedBy(customer));
    }

    /**
     * 고객 상세정보 조회
     * @param customerId 페이징 정보
     * @return 고객의 상세 정보
     */
    @Transactional
    public CustomerResponse.OfRetrieveDetailCustomer retrieveDetailCustomers(Long customerId) {

        Customer targetCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 ID 입니다."));

        return CustomerResponse.OfRetrieveDetailCustomer.convertedBy(targetCustomer);
    }
}
