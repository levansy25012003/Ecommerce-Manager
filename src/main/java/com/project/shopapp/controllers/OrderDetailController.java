package com.project.shopapp.controllers;


import com.project.shopapp.dtos.OrderDetailDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {

    @PostMapping("")
    public ResponseEntity<?> createOderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO,
                                              BindingResult result) {
        return ResponseEntity.ok("Create order detail successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") Long id) {
        return ResponseEntity.ok("Lấy chi tiết 1 đơn hàng: " + id);
    }

    // Lấy ra danh sách các order_details của 1 order nào đó.
    @GetMapping("/order/{orderId}")
    public  ResponseEntity<?> getOderDetails(@Valid @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok("Get order detail with = " + orderId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id,
                                               @RequestBody OrderDetailDTO newOrderDetailDaTa) {
        return ResponseEntity.ok("Update order detail with id: " + id + "newOrderDetailDaTa" + newOrderDetailDaTa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable("id") Long id) {
        return ResponseEntity.noContent().build();
    }

}
