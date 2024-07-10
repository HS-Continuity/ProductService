package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveAdvertisementProductResponse;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;

    public List<RetrieveAdvertisementProductResponse> retrieveAdvertisementProductList () {
        List<RetrieveAdvertisementProductResponse> advertisementProductList = productAdvertisementServiceRepository.findRandomActiveAdvertisementProducts();
        return advertisementProductList;
    }
}
