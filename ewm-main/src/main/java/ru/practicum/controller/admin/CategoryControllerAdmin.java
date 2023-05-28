package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryControllerAdmin {
    CategoryService categoryService;
    CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        Category newCategory = categoryMapper.fromDto(categoryDto);
        Category createdCategory = categoryService.createCategoryAdmin(newCategory);
        return categoryMapper.toDto(createdCategory);
    }

    @DeleteMapping("{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("catId") Integer catId) {
        categoryService.deleteCategoryByIdAdmin(catId);
    }

    @PatchMapping("{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Integer catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        Category newCategory = categoryMapper.fromDto(categoryDto);
        Category updatedCategory = categoryService.updateCategoryAdmin(catId, newCategory);
        return categoryMapper.toDto(updatedCategory);
    }
}
