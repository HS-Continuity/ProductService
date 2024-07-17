package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.messaging.message.AdvertisementEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementManagementService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 상당노출신청상품리스트 조회
     * @param customerId
     * @return
     */
    @Transactional
    public List<AdvertisementResponse.OfRetrieve> retrieveAppliedProduct(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );
        List<ProductAdvertisementService> advertisementProduct =
                productAdvertisementServiceRepository.findAllAdvertisementProduct(customerId);

        return advertisementProduct.stream().map(appliedProduct ->
                AdvertisementResponse.OfRetrieve.convertedBy(appliedProduct)).collect(Collectors.toList());
    }

    /**
     * 상품광고서비스 신청
     * @param //advertisementRequest
     * @return
     */
    @Transactional
    public void registerAdvertisement(AdvertisementRequest.OfRegister registerRequest) {
        Product product = productRepository.findById(registerRequest.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

       ProductAdvertisementService productAdvertisement = registerRequest.toEntity(product);
       productAdvertisementServiceRepository.save(productAdvertisement);

       AdvertisementEventMessage advertisementEventMessage = registerRequest.toEventMessage();
       kafkaTemplate.send("advertisement-topic", advertisementEventMessage);
    }
}
