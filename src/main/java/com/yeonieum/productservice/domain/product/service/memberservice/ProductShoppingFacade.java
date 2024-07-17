package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.service.CartProductService;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingFacade {

    private final StockSystemService stockSystemService;
    private final ProductShoppingService productShoppingService;
    private final CartProductService cartProductService;

    /**
     * 카테고리의 상품 목록 조회
     * @param productCategoryId 조회할 상품 카테고리 ID
     * @param isCertification 인증된 상품만 조회할지 여부
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    public Page<ProductShoppingResponse.OfRetrieveCategoryWithProduct> retrieveCategoryWithProducts(Long productCategoryId, ActiveStatus isCertification, Pageable pageable) {

        Page<ProductShoppingResponse.OfRetrieveCategoryWithProduct> retrieveCategoryWithProducts = productShoppingService.retrieveCategoryWithProducts(productCategoryId, isCertification, pageable);

        List<ProductShoppingResponse.OfSearchProductInformation> searchProductInformationDtoList
                = retrieveCategoryWithProducts.getContent().get(0).getSearchProductInformationDtoList();

        List<Long> productIdList = searchProductInformationDtoList.stream().map(dto -> {
                                        return dto.getProductId();
                                    }).collect(Collectors.toList());

        List<Boolean> isSoldOutList = stockSystemService.bulkCheckAvailableOrderProduct(productIdList);

        for(int i = 0; i < searchProductInformationDtoList.size(); i++) {
            searchProductInformationDtoList.get(i).changeIsSoldOut(!isSoldOutList.get(i));
        }

        return retrieveCategoryWithProducts;
    }

    /**
     * 상세 카테고리의 상품 목록 조회
     * @param productDetailCategoryId 조회할 상세 카테고리의 ID
     * @param isCertification 인증된 상품만 조회할지 여부
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    public Page<ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct> retrieveDetailCategoryWithProducts(Long productDetailCategoryId, ActiveStatus isCertification, Pageable pageable) {

        Page<ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct> retrieveDetailCategoryWithProducts = productShoppingService.retrieveDetailCategoryWithProducts(productDetailCategoryId, isCertification, pageable);


        List<ProductShoppingResponse.OfSearchProductInformation> searchProductInformationDtoList = retrieveDetailCategoryWithProducts.getContent().get(0).getSearchProductInformationDtoList();
        List<Long> productIdList = searchProductInformationDtoList.stream().map(dto -> {
                    return dto.getProductId();
                }).collect(Collectors.toList());
        List<Boolean> isSoldOutList = stockSystemService.bulkCheckAvailableOrderProduct(productIdList);
        for(int i = 0; i < searchProductInformationDtoList.size(); i++) {
            searchProductInformationDtoList.get(i).changeIsSoldOut(!isSoldOutList.get(i));
        }
        return retrieveDetailCategoryWithProducts;
    }

    /**
     * 키워드로 상품 조회
     * @param keyword 상품 키워드(이름)
     * @param pageable 페이징 정보
     * @return 해당 키워드의 상품들 정보
     */
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveKeywordWithProducts(String keyword, Pageable pageable) {
        Page<ProductShoppingResponse.OfSearchProductInformation> retrieveKeywordWithProducts = productShoppingService.retrieveKeywordWithProductsDto(keyword, pageable);

        List<ProductShoppingResponse.OfSearchProductInformation> searchProductInformationDtoList = retrieveKeywordWithProducts.getContent();

        List<Long> productIdList = searchProductInformationDtoList.stream().map(dto -> dto.getProductId())
                .collect(Collectors.toList());

        List<Boolean> isSoldOutList = stockSystemService.bulkCheckAvailableOrderProduct(productIdList);

        for(int i = 0; i < searchProductInformationDtoList.size(); i++) {
            searchProductInformationDtoList.get(i).changeIsSoldOut(!isSoldOutList.get(i));
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
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveCustomerWithProductsDto(Long customerId, Long detailCategoryId, Pageable pageable) {

        Page<ProductShoppingResponse.OfSearchProductInformation> retrieveCustomerWithProducts = productShoppingService.retrieveCustomerWithProductsDto(customerId, detailCategoryId, pageable);

        for(ProductShoppingResponse.OfSearchProductInformation searchProductInformationDto : retrieveCustomerWithProducts) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(searchProductInformationDto.getProductId());
            searchProductInformationDto.changeIsSoldOut(!isSoldOut);
        }
        return retrieveCustomerWithProducts;
    }

    /**
     * 장바구니 상품 조회
     * @param memberId 회원 ID
     * @param cartTypeId 장바구니 타입 ID
     * @return 장바구니 상품 정보
     */
    public List<CartProductResponse.OfRetrieveCartProduct> retrieveAllCartProducts(String memberId, Long cartTypeId) {

        List<CartProductResponse.OfRetrieveCartProduct> ofRetrieveCartProducts = cartProductService.retrieveAllCartProducts(memberId, cartTypeId);

        for(CartProductResponse.OfRetrieveCartProduct cartProduct : ofRetrieveCartProducts) {
            boolean isSoldOut = stockSystemService.checkAvailableOrderProduct(cartProduct.getProductId());
            cartProduct.changeIsSoldOut(!isSoldOut);
        }

        return ofRetrieveCartProducts;
    }
}
