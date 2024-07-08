package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductManagementService {
    boolean registerProduct(ProductManagementRequest.RegisterDto registerRequestDto, String imageUrl);
    boolean deleteProduct(Long productId);
    boolean modifyProduct(Long productId, ProductManagementRequest.ModifyDto modifyDto);
    List<ProductManagementResponse.RetrieveDto> retrieveCustomersProducts(Long customerId);
    ProductManagementResponse.RetrieveDto retrieveProductDetail(Long productId);
    boolean uploadProductImageUrl(Long productId, String imageUrl);
    boolean uploadProductDetailImages(Long productId, String imageUrl);
    boolean uploadCertificationImage(Long productId, String imageUrl , ProductManagementRequest.Certification certification);
    boolean deleteProductDetailImage(Long productDetailImageId);
}
