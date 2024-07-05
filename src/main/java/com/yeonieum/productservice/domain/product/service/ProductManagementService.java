package com.yeonieum.productservice.domain.product.service;

import com.yeonieum.productservice.domain.product.dto.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.ProductManagementResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductManagementService {
    boolean registerProduct(ProductManagementRequest.RegisterDto registerRequestDto, String imageUrl);
    boolean deleteProduct(Long productId);
    boolean modifyProduct(Long productId, ProductManagementRequest.ModifyDto modifyDto);
    List<ProductManagementResponse.RetrieveDto> retrieveCustomersProducts(Long customerId);
    ProductManagementResponse.RetrieveDto retrieveProductDetail(Long productId);
    boolean uploadProductImage(Long productId, MultipartFile imageFile);
    boolean uploadProductDetailImages(Long productId, MultipartFile imageFile);
    boolean uploadCertificationImage(Long productId, MultipartFile imageFile);

}
