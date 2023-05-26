package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicController {

    CompilationService compilationService;
    CompilationMapper compilationMapper;
    CategoryService categoryService;
    CategoryMapper categoryMapper;
    EventService eventService;
    EventMapper eventMapper;
//    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/compilations")
    public List<CompilationDto> getEventCompilations(@RequestParam(required = false) Boolean pinned,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        List<Compilation> result = compilationService.getEventCompilationsPublic(pinned, from, size);
        return compilationMapper.toDtoList(result);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getEventCompilationById(@PathVariable("compId") Integer compId) {
        Compilation result = compilationService.getEventCompilationByIdPublic(compId);
        return compilationMapper.toDto(result);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        List<Category> result = categoryService.getCategoriesPublic(from, size);
        return categoryMapper.toDtoList(result);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Integer catId) {
        Category result = categoryService.findCategoryByIdPublic(catId);
        return categoryMapper.toDto(result);
    }

    @GetMapping("/events")
    public List<EventShortDto> getPublicEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") List<Integer> categories,
            @RequestParam(required = false) boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "ALL") String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        SortEnum sortEnum = SortEnum.findByValueOrThrowException(sort);

//        String resultStart = java.net.URLDecoder.decode(rangeStart, StandardCharsets.UTF_8);
//        LocalDateTime start = LocalDateTime.parse(resultStart, dateTimeFormatter);
//
//        String resultEnd = java.net.URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8);
//        LocalDateTime end = LocalDateTime.parse(resultEnd, dateTimeFormatter);

        List<Event> result = eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sortEnum, from, size, request.getRemoteAddr());
        return eventMapper.toShortDtoList(result);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getPublicEventById(@PathVariable("id") Integer id, HttpServletRequest request) {
        Event result = eventService.getEventByIdPublic(id, request.getRemoteAddr());
        return eventMapper.toFullDto(result);
    }
}
