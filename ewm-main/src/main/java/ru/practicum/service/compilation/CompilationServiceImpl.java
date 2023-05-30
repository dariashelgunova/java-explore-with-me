package ru.practicum.service.compilation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundObjectException;
import ru.practicum.model.Compilation;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;

    public List<Compilation> getEventCompilationsPublic(Boolean pinned, Integer from, Integer size) {
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(size, from, null);
        if (pinned == null) {
            return compilationRepository.findBy(pageable);
        } else {
            return compilationRepository.findByPinned(pinned, pageable);
        }
    }

    public Compilation getEventCompilationByIdPublic(Integer compId) {
        return getEventCompilationByIdOrThrowException(compId);
    }

    private Compilation getEventCompilationByIdOrThrowException(Integer compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundObjectException("Объект не был найден"));
    }

    public Compilation createCompilationAdmin(Compilation compilation) {
        return compilationRepository.save(compilation);
    }

    public void deleteCompilationByIdAdmin(Integer compId) {
        getEventCompilationByIdOrThrowException(compId);
        compilationRepository.deleteById(compId);
    }

    public Compilation updateCompilationAdmin(Integer compId, Compilation compilation) {
        Compilation oldCompilation = getEventCompilationByIdPublic(compId);
        return changeCompilationFields(oldCompilation, compilation);
    }

    private Compilation changeCompilationFields(Compilation oldCompilation, Compilation newCompilation) {
        if (newCompilation.getPinned() != null) {
            oldCompilation.setPinned(newCompilation.getPinned());
        }
        if (StringUtils.isNotBlank(newCompilation.getTitle())) {
            oldCompilation.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.getEvents() != null) {
            oldCompilation.setEvents(newCompilation.getEvents());
        }
        return compilationRepository.save(oldCompilation);
    }
}
