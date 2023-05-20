package ru.practicum.service.category;

import ru.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategoryAdmin(Category category);
    void deleteCategoryByIdAdmin(Integer catId);
    Category updateCategoryAdmin(Integer catId, Category newCategory);
    List<Category> getCategoriesPublic(Integer from, Integer size);
    Category findCategoryByIdPublic(Integer catId);
}
