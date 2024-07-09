package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleProductResponse;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimeSaleRequest;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimeSale;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ProductTimeSaleRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.messaging.message.TimeSaleEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<RetrieveTimeSaleResponse> retrieveTimeSaleProducts(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );

        List<RetrieveTimeSaleResponse> productTimeSaleList = productRepository.findAllTimeSaleByCustomerId(customerId);
        return productTimeSaleList;
    }

    /**
     * 등록타임세일 상세 조회
     * @param
     * @return
     */
    @Transactional
    public RetrieveTimeSaleResponse retrieveCustomersTimeSale(Long timeSaleId) {
        RetrieveTimeSaleResponse retrieveTimeSaleResponse = productTimeSaleRepository.findTimeSaleProduct(timeSaleId);
        return retrieveTimeSaleResponse;
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


    // 타임세일 중인 상품리스트 조회
    @Transactional
    public List<RetrieveTimeSaleProductResponse> retrieveTimeSaleProducts(Pageable pageable) {
        List<RetrieveTimeSaleResponse> timeSaleList = productRepository.findAllTimeSaleProduct(pageable);
        /**
         * ==========================================================================================
         *                       리뷰 레포지토리 주입받아서 리뷰 정보 조회 후 응답데이터 구성 예정
         * ==========================================================================================
         */

        List<RetrieveTimeSaleProductResponse> timeSaleProductList = timeSaleList.stream().map(
                timeSale -> RetrieveTimeSaleProductResponse.builder()
                        .retrieveTimeSaleResponse(timeSale)
                        .averageRating(4.5)
                        .reviewCount(1000)
                        .build())
                .collect(Collectors.toList());

        return timeSaleProductList;
    }
}


class SearchCriteria {
    private String keyword;
    private String category;
    private String sort;
    private String order;
    private int pageNumber;
    private int pageSize;
    private String searchType;
    private String searchKeyword;
}
