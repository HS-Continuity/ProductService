package com.yeonieum.productservice.domain.product.service;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.RegisterAdvertisementRequestDto;
import com.yeonieum.productservice.domain.product.dto.RetrieveAdvertisementProductResponseDto;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;
    // 상당노출신청상품리스트 조회
    // 상단노출신청하기
    public List<RetrieveAdvertisementProductResponseDto> retrieveAppliedProduct(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );
        List<RetrieveAdvertisementProductResponseDto> advertisementProduct =
                productRepository.findAllAdvertismentProduct(customerId);

        return advertisementProduct;
    }

    public boolean registerAdvertisement(RegisterAdvertisementRequestDto registerAdvertisementRequestDto) {
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
       return true;
    }
}
