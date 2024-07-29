package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.memberservice.TimesaleResponseForMember;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesaleManagementFacade {
    private final StockSystemService stockSystemService;
    private final TimesaleManagementService timesaleManagementService;

    @Transactional
    public Page<TimesaleResponseForMember.OfRetrieve> retrieveTimesaleProducts(Pageable pageable) {
        Page<TimesaleResponseForMember.OfRetrieve> result = timesaleManagementService.retrieveTimesaleProducts(pageable);
        List<Long> productIdList = result.getContent().stream().map(dto -> dto.getProductId()).collect(Collectors.toList());

        Map<Long, Boolean> isSoldOutMap = stockSystemService.bulkCheckAvailableOrderProduct(productIdList);

        // Page 객체의 content에 있는 상품 목록을 조회하여 상품의 판매 가능 여부를 확인하고, 해당 정보를 변경한 후 반환
        for (int i = 0; i < result.getContent().size(); i++) {
            result.getContent().get(i).changeSoldOut(!isSoldOutMap.get(result.getContent().get(i).getProductId()));
        }

        return timesaleManagementService.retrieveTimesaleProducts(pageable);
    }


    @Transactional
    public TimesaleResponseForMember.OfRetrieve retrieveTimesaleProduct(Long productTimesaleId) {
        TimesaleResponseForMember.OfRetrieve result = timesaleManagementService.retrieveTimesaleProduct(productTimesaleId);
        boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(result.getProductId());
        result.changeSoldOut(!isSoldOut);

        return result;
    }


}
