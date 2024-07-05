package com.yeonieum.productservice.domain.category.dto.category;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RetrieveAllCategoryDto {

    private Long productCategoryId;
    private String categoryName;

}
