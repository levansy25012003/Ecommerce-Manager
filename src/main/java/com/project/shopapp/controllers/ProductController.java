package com.project.shopapp.controllers;


import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.impl.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProduct(@RequestParam("page") int page,
                                                             @RequestParam("limit") int limit) {
        // Tạo pageAble từ thông tin trang và giới hạn.
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);

        // Lấy ra tổng số trang
        int totalPages = productPage.getTotalPages();

        // Lấy ra danh sách sản phẩm
        List<ProductResponse> productList = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                                        .products(productList)
                                        .totalPages(totalPages)
                                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product exitingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(exitingProduct));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result
                                .getFieldErrors()
                                .stream()
                                .map(FieldError::getDefaultMessage)
                                .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        }
        catch (Exception e) {
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long productId,
                                          @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product exitingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<>() : files; // Xử lý tránh bị nullPointer
            List<ProductImage> productImages = new ArrayList<>();
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT)
                return ResponseEntity.badRequest().body("You can only upload maximun 5 images");
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file
                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Maximum size is 10MB");
                }
                // Kiểm định dạng file
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be image");
                }
                // Lưu file và cập nhật trong thumbnail trong DTO
                String filename = storeFile(file);
                // Lưu vào đối tượng product trong DB => sẽ làm sau
                // Lưu vào bảng product_images
                ProductImage productImage = productService.createProductImage(
                        exitingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageURL(filename)
                                .build());
                productImages.add(productImage); // lấy danh sách các ảnh đã thêm
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // return null;
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) && file.getOriginalFilename() == null) {
            throw new IOException("Invalid image file format");
        }
        // Lấy tên file
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Thêm UUID và trước tên file để đảm bào file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        // Đường dẫn đến thư mục mà bạn muốn lưu file
        Path uploadDir = Paths.get("uploads");

        // Kiểm tra và tạo thư mục nếu không tồn tại.
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Lấy đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);

        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    // Kiểm tra xem có file upload có là file ảnh hay không.
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteProductById(@PathVariable("id")  long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("This is product: %d", id));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generaFakeProduct")
    public ResponseEntity<String> generaFakeProduct() {
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(2, 5))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Products created successfully");

    }

}
