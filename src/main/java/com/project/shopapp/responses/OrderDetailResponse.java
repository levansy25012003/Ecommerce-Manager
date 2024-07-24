package com.project.shopapp.responses;


import com.project.shopapp.models.OrderDetail;
import jakarta.persistence.*;
import lombok.*;

@Data // create toString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long id;

    @JoinColumn(name = "order_id")
    private Long orderId;

    @JoinColumn(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Float price;

    @Column(name = "number_of_products")
    private int numberOfProducts;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "color")
    private String color;

    public static OrderDetailResponse fromOderDetail(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                    .id(orderDetail.getId())
                    .orderId(orderDetail.getOrder().getId())
                    .productId(orderDetail.getProduct().getId())
                    .price(orderDetail.getPrice())
                    .numberOfProducts(orderDetail.getNumberOfProducts())
                    .totalMoney(orderDetail.getTotalMoney())
                    .color(orderDetail.getColor())
                    .build();
    }

}
