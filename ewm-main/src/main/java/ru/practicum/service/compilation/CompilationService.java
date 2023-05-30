package ru.practicum.service.compilation;

import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<Compilation> getEventCompilationsPublic(Boolean pinned, Integer from, Integer size);

    Compilation getEventCompilationByIdPublic(Integer compId);

    Compilation createCompilationAdmin(Compilation compilation);

    void deleteCompilationByIdAdmin(Integer compId);

    Compilation updateCompilationAdmin(Integer compId, Compilation compilation);
}
