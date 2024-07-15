package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductManagementService {
    boolean checkCertificationValidation(Long serialNumber);
    boolean registerProduct(ProductManagementRequest.RegisterDto registerRequestDto, String imageUrl);
    boolean deleteProduct(Long productId, Long customerId);
    boolean modifyProduct(Long productId, Long customerId, ProductManagementRequest.ModifyDto modifyDto);
    List<ProductManagementResponse.RetrieveDto> retrieveCustomersProducts(Long customerId, ActiveStatus activeStatus);
    ProductManagementResponse.RetrieveDto retrieveProductDetail(Long productId);
    boolean uploadProductImageUrl(Long productId, String imageUrl);
    boolean uploadProductDetailImages(Long productId, ProductManagementRequest.DetailImageList deleteList, List<String> imageUrlList);
    boolean uploadCertificationImage(Long productId, String imageUrl , ProductManagementRequest.Certification certification);
    List<ProductManagementResponse.RetrieveDetailImage> retrieveDetailImage(Long productId);
}
