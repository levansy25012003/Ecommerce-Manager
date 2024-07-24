package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {

    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's ID must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long productId;

    @Min(value = 0, message = "Price's ID must be > 0")
    private  Float price;

    @JsonProperty("number_of_product")
    @Min(value = 1, message = "Number Of Product's ID must be > 0")
    private int numberOfProduct;

    @JsonProperty("total_money")
    private Float totalMoney;
    private  String color;

}
