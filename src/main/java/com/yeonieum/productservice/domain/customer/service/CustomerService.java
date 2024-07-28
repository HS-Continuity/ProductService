package com.yeonieum.productservice.domain.customer.service;

import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.exception.CustomerException;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.yeonieum.productservice.domain.customer.exception.CustomerExceptionCode.CUSTOMER_NOT_FOUND;

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
     * @param customerId 고객 ID
     * @return 고객의 상세 정보
     */
    @Transactional
    public CustomerResponse.OfRetrieveDetailCustomer retrieveDetailCustomers(Long customerId) {

        Customer targetCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return CustomerResponse.OfRetrieveDetailCustomer.convertedBy(targetCustomer);
    }

    /**
     * 업체의 배송비 조회
     * @param customerId 고객 ID
     * @return 업체의 배송비 가격
     */
    @Transactional
    public int retrieveDeliveryFee(Long customerId) {

        Customer targetCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return targetCustomer.getDeliveryFee();
    }
}
