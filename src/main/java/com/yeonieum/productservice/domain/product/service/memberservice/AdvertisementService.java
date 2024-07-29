package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.domain.product.repository.ProductAdvertisementServiceRepository;
import com.yeonieum.productservice.domain.product.repository.ServiceStatusRepository;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final ProductAdvertisementServiceRepository productAdvertisementServiceRepository;
    private final ServiceStatusRepository serviceStatusRepository;

    public List<AdvertisementResponse.OfRetrieve> retrieveAdvertisementProductList () {
        ServiceStatus status = serviceStatusRepository.findByStatusName(ServiceStatusCode.IN_PROGRESS.getCode());
        List<ProductAdvertisementService> advertisementProductList = productAdvertisementServiceRepository.findRandomActiveAdvertisements(ServiceStatusCode.IN_PROGRESS.getCode());
        return advertisementProductList.stream().map(productAdvertisementService ->
                AdvertisementResponse.OfRetrieve.convertedBy(productAdvertisementService)).collect(Collectors.toList());
    }
}
