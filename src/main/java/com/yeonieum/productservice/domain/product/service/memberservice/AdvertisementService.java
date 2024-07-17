package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;

    public List<AdvertisementResponse.OfRetrieve> retrieveAdvertisementProductList () {
        List<ProductAdvertisementService> advertisementProductList = productAdvertisementServiceRepository.findRandomActiveAdvertisementProducts();
        return advertisementProductList.stream().map(productAdvertisementService ->
                AdvertisementResponse.OfRetrieve.convertedBy(productAdvertisementService)).collect(Collectors.toList());
    }
}
