package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductShoppingFacade {

    private final StockSystemService stockSystemService;
    private final ProductShoppingService productShoppingService;

    /**
     * 카테고리 조회시, 해당 (상세)카테고리의 상품 조회
     * @param productCategoryId 상품 카테고리 ID
     * @param isCertification 인증서 여부
     * @param pageable 페이징 값
     * @return 카테고리에 포함되는 상품들의 정보
     */
    public ProductShoppingResponse.RetrieveCategoryWithProductsDto retrieveCategoryWithProducts(Long productCategoryId, ActiveStatus isCertification, Pageable pageable) {

        ProductShoppingResponse.RetrieveCategoryWithProductsDto retrieveCategoryWithProducts = productShoppingService.retrieveCategoryWithProducts(productCategoryId, isCertification, pageable);
        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = retrieveCategoryWithProducts.getSearchProductInformationDtoList();

        for(ProductShoppingResponse.SearchProductInformationDto searchProductInformationDto : searchProductInformationDtoList) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(searchProductInformationDto.getProductId());
            searchProductInformationDto.changeIsSoldOut(!isSoldOut);
        }
        return retrieveCategoryWithProducts;
    }

    /**
     * 상세 카테고리 조회시, 해당 카테고리의 상품 조회
     * @param productDetailCategoryId 상세 카테고리 ID
     * @param isCertification 인증서 여부
     * @param pageable 페이징 값
     * @return 상세 카테고리에 포함되는 상품들의 정보
     */
    public ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto retrieveDetailCategoryWithProducts(Long productDetailCategoryId, ActiveStatus isCertification, Pageable pageable) {

        ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto retrieveDetailCategoryWithProducts = productShoppingService.retrieveDetailCategoryWithProducts(productDetailCategoryId, isCertification, pageable);
        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = retrieveDetailCategoryWithProducts.getSearchProductInformationDtoList();

        for(ProductShoppingResponse.SearchProductInformationDto searchProductInformationDto : searchProductInformationDtoList) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(searchProductInformationDto.getProductId());
            searchProductInformationDto.changeIsSoldOut(!isSoldOut);
        }
        return retrieveDetailCategoryWithProducts;
    }

    /**
     * 키워드로 상품 조회
     * @param keyword 상품 키워드(이름)
     * @param pageable 페이징 정보
     * @return 해당 키워드의 상품들 정보
     */
    public ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveKeywordWithProducts(String keyword, Pageable pageable) {

        ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveKeywordWithProducts = productShoppingService.retrieveKeywordWithProductsDto(keyword, pageable);
        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = retrieveKeywordWithProducts.getSearchProductInformationDtoList();

        for(ProductShoppingResponse.SearchProductInformationDto searchProductInformationDto : searchProductInformationDtoList) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(searchProductInformationDto.getProductId());
            searchProductInformationDto.changeIsSoldOut(!isSoldOut);
        }
        return retrieveKeywordWithProducts;
    }

    /**
     * 업체별 상품 조회
     * @param customerId 고객 ID
     * @param detailCategoryId 상세 카테고리 ID
     * @param pageable 페이징 정보
     * @return 업체 상품들의 정보
     */
    public ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveCustomerWithProductsDto(Long customerId, Long detailCategoryId, Pageable pageable) {

        ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveCustomerWithProducts = productShoppingService.retrieveCustomerWithProductsDto(customerId, detailCategoryId, pageable);
        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = retrieveCustomerWithProducts.getSearchProductInformationDtoList();

        for(ProductShoppingResponse.SearchProductInformationDto searchProductInformationDto : searchProductInformationDtoList) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(searchProductInformationDto.getProductId());
            searchProductInformationDto.changeIsSoldOut(!isSoldOut);
        }
        return retrieveCustomerWithProducts;
    }
}
