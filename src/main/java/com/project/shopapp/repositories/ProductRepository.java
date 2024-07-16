package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Xem sản phẩm với cái tên có tồn tại không
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable); // Phân trang

}
