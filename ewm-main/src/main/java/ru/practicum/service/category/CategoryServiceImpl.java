package ru.practicum.service.category;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Category;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.event.EventService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    EventService eventService;

    public Category createCategoryAdmin(Category category) {
        checkIfNameIsUnique(category);
        return  categoryRepository.save(category);
    }

    public void deleteCategoryByIdAdmin(Integer catId) {
        Category category = getCategoryByIdOrThrowException(catId);
        eventService.checkIfThereAreNoEventsInCategory(category);
        categoryRepository.deleteById(catId);
    }

    public Category updateCategoryAdmin(Integer catId, Category newCategory) {
        Category oldCategory = getCategoryByIdOrThrowException(catId);
        return changeCategoryFields(oldCategory, newCategory);
    }

    private Category changeCategoryFields(Category oldCategory, Category newCategory) {
        if (newCategory.getName() != null) {
            if (!Objects.equals(oldCategory.getName(), newCategory.getName())) {
                checkIfNameIsUnique(newCategory);
            }
            oldCategory.setName(newCategory.getName());
        }
        return categoryRepository.save(oldCategory);
    }

    private Category getCategoryByIdOrThrowException(Integer catId) {
        if (catId == null) {
            return null;
        }
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

    public List<Category> getCategoriesPublic(Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        return categoryRepository.findBy(pageable);
    }

    public Category findCategoryByIdPublic(Integer catId) {
        return getCategoryByIdOrThrowException(catId);
    }

    private void checkIfNameIsUnique(Category category) {
        List<Category> result = categoryRepository.findByName(category.getName());
        if (!result.isEmpty() && (category.getId() == null | !Objects.equals(result.get(0).getId(), category.getId()))) {
            throw new ConflictException("Имя категории не может повторяться");
        }
    }

}
