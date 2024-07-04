package com.yeonieum.productservice.domain.category.dto.category;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RegisterCategoryDto {

    private String categoryName;

}
