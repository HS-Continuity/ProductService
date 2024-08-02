package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.exception.CustomerException;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ServiceStatusRepository;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import com.yeonieum.productservice.infrastructure.messaging.message.AdvertisementEventMessage;
import lombok.RequiredArgsConstructor;
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
public class AdvertisementManagementService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;
    private final ServiceStatusRepository serviceStatusRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 상당노출신청상품리스트 조회
     * @param customerId
     * @return
     */
    @Transactional
    public List<AdvertisementResponse.OfRetrieve> retrieveAppliedProduct(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                        () -> new CustomerException(CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<ProductAdvertisementService> advertisementProduct =
                productAdvertisementServiceRepository.findAllAdvertisementProduct(customerId);

        return advertisementProduct.stream().map(appliedProduct ->
                AdvertisementResponse.OfRetrieve.convertedBy(appliedProduct)).collect(Collectors.toList());
    }

    /**
     * 상품광고서비스 신청
     * @param registerRequest
     * @return
     */
    @Transactional
    public void registerAdvertisement(Long customerId, AdvertisementRequest.OfRegister registerRequest) {
        Product product = productRepository.findByProductIdAndCustomer_CustomerId(registerRequest.getProductId(), customerId);
        if(product == null) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.PENDING.getCode());
        ProductAdvertisementService productAdvertisement = registerRequest.toEntity(product, status);
        productAdvertisementServiceRepository.save(productAdvertisement);

        AdvertisementEventMessage advertisementEventMessage = registerRequest.toEventMessage();
        //kafkaTemplate.send("advertisement-topic", advertisementEventMessage);
    }

    /**
     * 고객의 광고신청 취소
     * @param advertisementId
     */
    @Transactional
    public void cancelAdvertisement(Long advertisementId) {
        ProductAdvertisementService advertisementService = productAdvertisementServiceRepository.findById(advertisementId).orElseThrow(
                () -> new ProductException(PRODUCT_ADVERTISEMENT_NOT_FOUNT,HttpStatus.NOT_FOUND));

        if(advertisementService.getServiceStatus().getStatusName() != ServiceStatusCode.PENDING.getCode()) {
            throw new ProductException(PRODUCT_ADVERTISEMENT_CANNOT_BE_CANCELED, HttpStatus.CONFLICT);
        }
        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.CANCELED.getCode());
        advertisementService.changeServiceStatus(status);
    }

    /**
     * 관리자의 광고신청 승인
     * @param advertisementId
     */
    @Transactional
    public void approveAdvertisement(Long advertisementId) {
        ProductAdvertisementService advertisementService = productAdvertisementServiceRepository.findById(advertisementId).orElseThrow(
                () -> new ProductException(PRODUCT_ADVERTISEMENT_NOT_FOUNT,HttpStatus.NOT_FOUND));

        if(advertisementService.getServiceStatus().getStatusName() != ServiceStatusCode.PENDING.getCode()) {
            throw new ProductException(PRODUCT_ADVERTISEMENT_CANNOT_BE_APPROVE, HttpStatus.CONFLICT);
        }
        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.APPROVE.getCode());
        advertisementService.changeServiceStatus(status);
    }
}
