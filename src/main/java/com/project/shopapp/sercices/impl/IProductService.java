package com.project.shopapp.sercices.impl;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException;
    public void deleteProduct(long id) throws DataNotFoundException;
    public Product getProductById(Long id) throws DataNotFoundException;
    public Page<Product> getAllProducts(PageRequest pageRequest);

    public boolean existsByName(String name);
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;
}
