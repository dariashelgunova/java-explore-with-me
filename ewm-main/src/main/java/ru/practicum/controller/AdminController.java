package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.event.EventService;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {

    CompilationService compilationService;
    CompilationMapper compilationMapper;
    CategoryService categoryService;
    CategoryMapper categoryMapper;
    UserService userService;
    UserMapper userMapper;
    EventService eventService;
    EventMapper eventMapper;

    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody NewCategoryDto categoryDto) {
        Category newCategory = categoryMapper.fromDto(categoryDto);
        Category createdCategory = categoryService.createCategoryAdmin(newCategory);
        return categoryMapper.toDto(createdCategory);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategoryById(@PathVariable("catId") Integer catId) {
        categoryService.deleteCategoryByIdAdmin(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Integer catId,
                                      @RequestBody CategoryDto categoryDto) {
        Category newCategory = categoryMapper.fromDto(categoryDto);
        Category updatedCategory = categoryService.updateCategoryAdmin(catId, newCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @GetMapping("/events")
    public List<EventFullDto> getAdminEvents(
            @RequestParam Integer[] users,
            @RequestParam String[] states,
            @RequestParam Integer[] categories,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<Event> result = eventService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventMapper.toFullDtoList(result);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventAndStatus(@PathVariable("eventId") Integer eventId,
                                             @RequestBody UpdateEventAdminRequest eventDto) {
        Event event = eventMapper.fromDto(eventDto, categoryService.findCategoryByIdPublic(eventDto.getCategory()));
        Event updatedEvent = eventService.updateEventAdmin(eventId, event);
        return eventMapper.toFullDto(updatedEvent);
    }

    @GetMapping("/users")
    public List<UserDto> getAdminUsers(
            @RequestParam Integer[] ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<User> result = userService.findUsersAdmin(ids, from, size);
        return userMapper.toDtoList(result);
    }

    @PostMapping("/users")
    public UserDto createAdminUsers(@RequestBody NewUserRequest userDto) {
        User newUser = userMapper.fromDto(userDto);
        User createdUser = userService.createUserAdmin(newUser);
        return userMapper.toDto(createdUser);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteAdminUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUserByIdAdmin(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto createAdminCompilation(@RequestBody NewCompilationDto compilationDto) {
        List<Event> events = eventService.findEventsByIds(compilationDto.getEvents());
        Compilation newCompilation = compilationMapper.fromDto(compilationDto, events);
        Compilation result = compilationService.createCompilationAdmin(newCompilation);
        return compilationMapper.toDto(result);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteAdminCompilationById(@PathVariable("compId") Integer compId) {
        compilationService.deleteCompilationByIdAdmin(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Integer compId,
                                            @RequestBody UpdateCompilationRequest compilationDto) {
        List<Event> events = eventService.findEventsByIds(compilationDto.getEvents());
        Compilation newCompilation = compilationMapper.fromDto(compilationDto, events);
        Compilation updatedCompilation = compilationService.updateCompilationAdmin(compId, newCompilation);
        return compilationMapper.toDto(updatedCompilation);
    }
}
