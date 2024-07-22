package com.project.shopapp.responses;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private int totalPages;
    List<ProductResponse> products;
}
