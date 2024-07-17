package com.yeonieum.productservice.global.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

    public static Pageable createPageable(int page, int size, String sort, String direction) {
        Sort.Direction dir = Sort.Direction.fromString(direction);
        switch (sort) {
            case "productName":
            case "productPrice":
            case "averageScore":
            case "reviewScore":
            case "createDate":
                break;
            default:
                sort = "productName";
                break;
        }
        return PageRequest.of(page, size, Sort.by(dir, sort));
    }
}

