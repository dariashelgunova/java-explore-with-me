package ru.practicum.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.service.compilation.CompilationService;
import ru.practicum.service.event.EventService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationControllerAdmin {
    CompilationService compilationService;
    CompilationMapper compilationMapper;
    EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createAdminCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        List<Event> events = eventService.findEventsByIds(compilationDto.getEvents());
        Compilation newCompilation = compilationMapper.fromDto(compilationDto, events);
        Compilation result = compilationService.createCompilationAdmin(newCompilation);
        return compilationMapper.toDto(result);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminCompilationById(@PathVariable("compId") Integer compId) {
        compilationService.deleteCompilationByIdAdmin(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Integer compId,
                                            @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        List<Event> events = eventService.findEventsByIds(compilationDto.getEvents());
        Compilation newCompilation = compilationMapper.fromDto(compilationDto, events);
        Compilation updatedCompilation = compilationService.updateCompilationAdmin(compId, newCompilation);
        return compilationMapper.toDto(updatedCompilation);
    }
}
