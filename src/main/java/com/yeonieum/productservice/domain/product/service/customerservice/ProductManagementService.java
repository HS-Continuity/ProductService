package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductManagementService {
    boolean checkCertificationValidation(Long serialNumber);
    void registerProduct(ProductManagementRequest.OfRegister registerRequestDto, String imageUrl);
    void deleteProduct(Long productId, Long customerId);
    boolean modifyProduct(Long productId, Long customerId, ProductManagementRequest.OfModifying ofModifying);
    Page<ProductManagementResponse.OfRetrieve> retrieveCustomersProducts(Long customerId, ActiveStatus activeStatus, Pageable pageable);
    ProductManagementResponse.OfRetrieveDetails retrieveProductDetail(Long productId);
    void uploadProductImageUrl(Long productId, String imageUrl);
    void uploadProductDetailImages(Long productId, ProductManagementRequest.OfDeleteDetailImageList deleteList, List<String> imageUrlList);
    void uploadCertificationImage(Long productId, String imageUrl , ProductManagementRequest.Certification certification);
    List<ProductManagementResponse.OfRetrieveDetailImage> retrieveDetailImage(Long productId);
    ProductManagementResponse.OfRetrieveProductOrder retrieveProductInformatino(Long productId);

    List<ProductManagementResponse.OfRetrieveProductOrder> bulkRetrieveProductInformatino(List<Long> productIdList);

}
