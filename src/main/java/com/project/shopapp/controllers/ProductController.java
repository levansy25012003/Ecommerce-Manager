package com.project.shopapp.controllers;


import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.sercices.impl.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;
    @GetMapping("")
    public ResponseEntity<String> getAllProduct(@RequestParam("page") int page,
                                                @RequestParam("limit") int limit) {
        return ResponseEntity.ok("Get product here");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getProductById(@PathVariable("id") String productId) {
        return ResponseEntity.ok("This is product: " + productId);
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
            return ResponseEntity.ok("Products created successfully");
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
        // Lấy tên file
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

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

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteProductById(@PathVariable("id")  long id) {
        return ResponseEntity.ok(String.format("This is product: %d", id));
    }
}
