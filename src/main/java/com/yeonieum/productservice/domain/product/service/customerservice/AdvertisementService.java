package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.RegisterAdvertisementRequestDto;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveAdvertisementProductResponseDto;
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

@Service
@RequiredArgsConstructor
public class AdvertisementService {
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
    public List<RetrieveAdvertisementProductResponseDto> retrieveAppliedProduct(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );
        List<RetrieveAdvertisementProductResponseDto> advertisementProduct =
                productRepository.findAllAdvertisementProduct(customerId);

        return advertisementProduct;
    }

    /**
     * 상품광고서비스 신청
     * @param registerAdvertisementRequestDto
     * @return
     */
    @Transactional
    public void registerAdvertisement(RegisterAdvertisementRequestDto registerAdvertisementRequestDto) {
        Product product = productRepository.findById(registerAdvertisementRequestDto.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

       ProductAdvertisementService productAdvertisement = ProductAdvertisementService.builder()
                .product(product)
                .startDate(registerAdvertisementRequestDto.getStartDate())
                .endDate(registerAdvertisementRequestDto.getEndDate())
                .isCompleted(ActiveStatus.ACTIVE)
                .build();

       productAdvertisementServiceRepository.save(productAdvertisement);
       AdvertisementEventMessage advertisementEventMessage = AdvertisementEventMessage.builder()
                .productId(registerAdvertisementRequestDto.getProductId())
                .startDate(registerAdvertisementRequestDto.getStartDate())
                .endDate(registerAdvertisementRequestDto.getEndDate())
                .build();

       kafkaTemplate.send("advertisement-topic", advertisementEventMessage);
    }
}
