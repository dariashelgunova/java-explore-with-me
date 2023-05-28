package ru.practicum.controller.publics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationControllerPublic {

    CompilationService compilationService;
    CompilationMapper compilationMapper;

    @GetMapping
    public List<CompilationDto> getEventCompilations(@RequestParam(required = false) Boolean pinned,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<Compilation> result = compilationService.getEventCompilationsPublic(pinned, from, size);
        return compilationMapper.toDtoList(result);
    }

    @GetMapping("/{compId}")
    public CompilationDto getEventCompilationById(@PathVariable("compId") Integer compId) {
        Compilation result = compilationService.getEventCompilationByIdPublic(compId);
        return compilationMapper.toDto(result);
    }
}
