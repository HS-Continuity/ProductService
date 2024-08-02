package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductManagementService {
    boolean checkCertificationValidation(Long serialNumber);
    void registerProduct(Long customerId, ProductManagementRequest.OfRegister registerRequestDto, MultipartFile defaultImage, List<MultipartFile> detailImageList);
    void  deleteProduct(Long customerId, Long productId);
    void modifyProduct(Long productId, Long customerId, ProductManagementRequest.OfModify ofModify);
    Page<ProductManagementResponse.OfRetrieve> retrieveCustomersProducts(Long customerId, ActiveStatus isEcoFriend, Long productId, String productName, String description, String origin, Integer price, ActiveStatus isPageVisibility, ActiveStatus isRegularSale, Integer baseDiscountRate, Integer regularDiscountRate, Pageable pageable);
    ProductManagementResponse.OfRetrieveDetails retrieveProductDetail(Long customerId, Long productId);
    void uploadProductImageUrl(Long productId, Long customerId, String imageUrl);
    void uploadProductDetailImages(Long productId, Long customerId, ProductManagementRequest.OfDeleteDetailImageList deleteList, List<String> imageUrlList);
    void uploadCertificationImage(Long productId, String imageUrl , ProductManagementRequest.Certification certification) throws IOException;
    List<ProductManagementResponse.OfRetrieveDetailImage> retrieveDetailImage(Long productId);
    ProductManagementResponse.OfRetrieveProductOrder retrieveProductInformation(Long productId);
    Map<Long, ProductManagementResponse.OfRetrieveProductOrder> bulkRetrieveProductInformation(List<Long> productIdList);
    List<ProductManagementResponse.OfOrderInformation> retrieveOrderInformation(StockUsageRequest.IncreaseStockUsageList increaseStockUsageList);
}
