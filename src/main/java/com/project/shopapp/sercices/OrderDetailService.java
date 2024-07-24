package com.project.shopapp.sercices;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.sercices.impl.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng với id: " + orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm với id: " + orderDetailDTO.getProductId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProduct())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        orderDetailRepository.save(orderDetail);
        return orderDetail;
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {

        OrderDetail exitingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy chi tiết đơn hàng id: " + id));
        Order exitingOder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy đơn hàng id: " + orderDetailDTO.getOrderId()));
        Product exitingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm id: " + orderDetailDTO.getProductId()));

        exitingOrderDetail.setOrder(exitingOder);
        exitingOrderDetail.setProduct(exitingProduct);
        exitingOrderDetail.setPrice(orderDetailDTO.getPrice());
        exitingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProduct());
        exitingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        exitingOrderDetail.setColor(orderDetailDTO.getColor());

        orderDetailRepository.save(exitingOrderDetail);
        return exitingOrderDetail;
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        OrderDetail oderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy order detail với id: " + id));
        return oderDetail;
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }


}
