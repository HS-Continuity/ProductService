package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductManagementService {
    boolean checkCertificationValidation(Long serialNumber);
    void registerProduct(ProductManagementRequest.OfRegister registerRequestDto, MultipartFile defaultImage, List<MultipartFile> detailImageList);
    void  deleteProduct(Long productId, Long customerId);
    void modifyProduct(Long productId, Long customerId, ProductManagementRequest.OfModify ofModify);
    Page<ProductManagementResponse.OfRetrieve> retrieveCustomersProducts(Long customerId, ActiveStatus activeStatus, Pageable pageable);
    ProductManagementResponse.OfRetrieveDetails retrieveProductDetail(Long productId);
    void uploadProductImageUrl(Long productId, String imageUrl);
    void uploadProductDetailImages(Long productId, ProductManagementRequest.OfDeleteDetailImageList deleteList, List<String> imageUrlList);
    void uploadCertificationImage(Long productId, String imageUrl , ProductManagementRequest.Certification certification) throws IOException;
    List<ProductManagementResponse.OfRetrieveDetailImage> retrieveDetailImage(Long productId);
    ProductManagementResponse.OfRetrieveProductOrder retrieveProductInformation(Long productId);
    List<ProductManagementResponse.OfRetrieveProductOrder> bulkRetrieveProductInformatino(List<Long> productIdList);
}
