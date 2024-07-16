package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data // ToString
@Getter
@Setter
@AllArgsConstructor // Constructor đầy đủ các đối số
@NoArgsConstructor // Constructor mặc định không đối
public class CategoryDTO {
    @NotEmpty(message = "Category's name can't is empty")
    private String name;
}
