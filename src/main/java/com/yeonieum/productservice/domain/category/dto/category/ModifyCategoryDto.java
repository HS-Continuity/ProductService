package com.yeonieum.productservice.domain.category.dto.category;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ModifyCategoryDto {

    private Long productCategoryId;
    private String categoryName;

}
