package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductShoppingFacade {

    private final StockSystemService stockSystemService;
    private final ProductShoppingService productShoppingService;



}
