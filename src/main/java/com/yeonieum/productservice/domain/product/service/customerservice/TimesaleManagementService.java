package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleResponseForCustomer;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleRequestForCustomer;
import com.yeonieum.productservice.domain.product.dto.memberservice.TimesaleResponseForMember;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ProductTimesaleRepository;
import com.yeonieum.productservice.domain.product.repository.ServiceStatusRepository;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import com.yeonieum.productservice.messaging.message.TimesaleEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesaleManagementService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductTimesaleRepository productTimesaleRepository;
    private final ServiceStatusRepository serviceStatusRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 고객의 등록타임세일 목록 조회
     * @param customerId
     * @return
     */
    @Transactional
    public List<TimesaleResponseForCustomer.OfRetrieve> retrieveTimesaleProducts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new IllegalArgumentException("존재하지않는 고객 요청입니다."));

        List<ProductTimesale> productTimesaleList = productRepository.findAllTimesaleByCustomerId(customerId);
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
    public TimesaleResponseForCustomer.OfRetrieve retrieveCustomersTimesale(Long timesaleId, Long customerId) {
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
    public void registerTimesale(TimesaleRequestForCustomer.OfRegister registerRequest, Long customerId) {
        Product product = productRepository.findByProductIdAndCustomer_CustomerId(registerRequest.getProductId(), customerId);
        if (product == null) {
            throw new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.");
        }

        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.PENDING.getCode());
        ProductTimesale productTimesale = registerRequest.toEntity(product, status);
        productTimesaleRepository.save(productTimesale);

        TimesaleEventMessage timesaleEventMessage = registerRequest.toEventMessage();
        kafkaTemplate.send("timesale-topic", timesaleEventMessage);
    }

    /**
     * 고객의 타임세일신청 취소
     * @param timesaleId
     */
    @Transactional
    public void cancelTimesale(Long timesaleId, Long customerId) {
        ProductTimesale productTimesale = productTimesaleRepository.findByIdWithProduct(timesaleId);
        if(productTimesale == null) {
            throw new IllegalArgumentException("존재하지 않는 타임세일입니다.");
        }

        Product product = productTimesale.getProduct();
        if (product == null || product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        if(productTimesale.getServiceStatus().getStatusName() != ServiceStatusCode.PENDING.getCode()) {
            throw new IllegalArgumentException("취소할 수 없는 상태입니다.");
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
}