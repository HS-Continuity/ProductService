package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleProductResponseDto;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimeSaleRequest;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimeSale;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ProductTimeSaleRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.messaging.message.TimeSaleEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSaleService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductTimeSaleRepository productTimeSaleRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 고객의 등록타임세일 목록 조회
     * @param customerId
     * @return
     */
    @Transactional
    public List<RetrieveTimeSaleProductResponseDto> retrieveTimeSaleProducts(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );

        List<RetrieveTimeSaleProductResponseDto> productTimeSaleList = productRepository.findAllTimeSaleProduct(customerId);
        return productTimeSaleList;
    }

    /**
     * 고객의 타임세일 등록
     * @param registerDto
     * @return
     */
    @Transactional
    public void registerTimeSale(TimeSaleRequest.RegisterDto registerDto) {
        Product product = productRepository.findById(registerDto.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        ProductTimeSale productTimeSale = ProductTimeSale.builder()
                .product(product)
                .discountRate(registerDto.getDiscountRate())
                .startDatetime(registerDto.getStartTime())
                .endDatetime(registerDto.getEndTime())
                .isCompleted(ActiveStatus.ACTIVE) // 기본ㅔ
                .build();

        productTimeSaleRepository.save(productTimeSale);

        TimeSaleEventMessage timeSaleEventMessage = TimeSaleEventMessage.builder()
                        .startDateTime(registerDto.getStartTime())
                        .endDateTime(registerDto.getEndTime())
                        .productId(registerDto.getProductId())
                        .build();

        kafkaTemplate.send("timesale-topic", timeSaleEventMessage);
    }

    /**
     * 고객의 타임세일상태 변경
     * @param modifyStatusDto
     */
    @Transactional
    public void modifyTimeSaleStatus(TimeSaleRequest.ModifyStatusDto modifyStatusDto) {
        ProductTimeSale productTimeSale = productTimeSaleRepository.findByProduct_ProductId(modifyStatusDto.getProductId());
        productTimeSale.changeIsCompleted(modifyStatusDto.getIsCompleted());
    }

}
