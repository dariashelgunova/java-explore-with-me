package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.StateAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"category", "confirmedRequests", "location", "initiator", "views"})
@ToString(exclude = {"category", "confirmedRequests", "location", "initiator", "views"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    int confirmedRequests;
    Boolean available;
    @Column(name = "created_on")
    LocalDateTime createdOn = LocalDateTime.now();
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    Location location;
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    State state = State.PENDING;
    String title;
    @Transient
    StateAction stateAction;
    @Transient
    int views;
    @Transient
    List<Compilation> compilations;
}
