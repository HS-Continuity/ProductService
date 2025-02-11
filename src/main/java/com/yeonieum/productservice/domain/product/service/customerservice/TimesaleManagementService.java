package com.yeonieum.productservice.domain.product.service.customerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.exception.CustomerException;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleResponseForCustomer;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleRequestForCustomer;
import com.yeonieum.productservice.domain.product.dto.memberservice.TimesaleResponseForMember;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ProductTimesaleRepository;
import com.yeonieum.productservice.domain.product.repository.ServiceStatusRepository;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import com.yeonieum.productservice.infrastructure.messaging.ProductEventProducer;
import com.yeonieum.productservice.infrastructure.messaging.message.TimesaleEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.yeonieum.productservice.domain.customer.exception.CustomerExceptionCode.CUSTOMER_NOT_FOUND;
import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.*;

@Service
@RequiredArgsConstructor
public class TimesaleManagementService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductTimesaleRepository productTimesaleRepository;
    private final ServiceStatusRepository serviceStatusRepository;

    /**
     * 고객의 등록타임세일 목록 조회
     * @param customerId
     * @return
     */
    @Transactional
    public List<TimesaleResponseForCustomer.OfRetrieve> retrieveTimesaleProducts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerException(CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<ProductTimesale> productTimesaleList = productTimesaleRepository.findAllTimesaleByCustomerId(customerId);
        return productTimesaleList.stream().map(timesale -> {
                    ServiceStatus status = timesale.getServiceStatus();
                    return TimesaleResponseForCustomer.OfRetrieve.convertedBy(timesale);
                })
                .collect(Collectors.toList());
    }

    /**
     * 등록타임세일 상세 조회
     * @param
     * @return
     */
    @Transactional
    public TimesaleResponseForCustomer.OfRetrieve retrieveCustomersTimesale(Long customerId, Long timesaleId) {
        ProductTimesale productTimesale = productTimesaleRepository.findByIdWithProduct(timesaleId);
        if(productTimesale == null) {
            throw new IllegalArgumentException("존재하지 않는 타임세일입니다.");
        }
        Product product = productTimesale.getProduct();
        if (product == null || product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        return TimesaleResponseForCustomer.OfRetrieve.convertedBy(productTimesale);
    }


    /**
     * 고객의 타임세일 등록
     * @param registerRequest
     * @return
     */
    @Transactional
    public Long registerTimesale(Long customerId, TimesaleRequestForCustomer.OfRegister registerRequest) throws JsonProcessingException {
        Product product = productRepository.findByProductIdAndCustomer_CustomerId(registerRequest.getProductId(), customerId);
        if(product == null) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if(productTimesaleRepository.existsByProductIdAndServiceStatus(registerRequest.getProductId())) {
            throw new IllegalArgumentException("이미 타임세일이 진행중인 상품입니다.");
        }

        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.PENDING.getCode());
        ProductTimesale productTimesale = registerRequest.toEntity(product, status);
        ProductTimesale savedEntity = productTimesaleRepository.save(productTimesale);

        return savedEntity.getProductTimesaleId();
    }

    /**
     * 고객의 타임세일신청 취소
     * @param timesaleId
     */
    @Transactional
    public void cancelTimesale(Long customerId, Long timesaleId) {
        ProductTimesale productTimesale = productTimesaleRepository.findByIdWithProduct(timesaleId);
        if(productTimesale == null) {
                throw new ProductException(PRODUCT_TIME_SALE_NOT_FOUNT, HttpStatus.NOT_FOUND);
        }

        Product product = productTimesale.getProduct();
        if (product == null || product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        if(!productTimesale.getServiceStatus().getStatusName().equals(ServiceStatusCode.PENDING.getCode())) {
            throw new ProductException(PRODUCT_TIME_SALE_CANNOT_BE_CANCELED, HttpStatus.CONFLICT);
        }
        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.CANCELED.getCode());
        productTimesale.changeServiceStatus(status);
    }


    // 타임세일 중인 상품리스트 조회
    @Transactional
    public Page<TimesaleResponseForMember.OfRetrieve> retrieveTimesaleProducts(Pageable pageable) {
        /**
         * ==========================================================================================
         *    리뷰 레포지토리 주입받아서 리뷰 정보 조회 후 응답데이터 구성 예정   --->    상품테이블에 집계 속성 적용하여 해결
         * ==========================================================================================
         */
        Page<ProductTimesale> timesaleList = productTimesaleRepository.findAllTimesaleProduct(pageable);
        return timesaleList.map(timesale -> TimesaleResponseForMember.OfRetrieve.convertedBy(timesale));
    }

    @Transactional
    public TimesaleResponseForMember.OfRetrieve retrieveTimesaleProduct(Long timesaleId) {
        ProductTimesale productTimesale = productTimesaleRepository.findById(timesaleId).orElseThrow(
                () -> new ProductException(PRODUCT_TIME_SALE_NOT_FOUNT, HttpStatus.NOT_FOUND));

        return TimesaleResponseForMember.OfRetrieve.convertedBy(productTimesale);
    }
}