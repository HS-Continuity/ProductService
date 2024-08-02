package com.yeonieum.productservice.domain.product.dto.customerservice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class DetailImageRequest {
    @Getter
    @NoArgsConstructor
    public static class OfCreation {
        List<ImageUrl> imageUrlList;
    }

    @Getter
    @Builder
    public static class ImageUrl {
        String imageUrl;
    }
}