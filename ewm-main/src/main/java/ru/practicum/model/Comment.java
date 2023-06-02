package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String text;
    @Column(name = "user_id")
    Integer userId;
    @Column(name = "event_id")
    Integer eventId;
    @Column(name = "created_on")
    LocalDateTime createdOn = LocalDateTime.now();
    @Column(name = "is_positive")
    Boolean isPositive;
    @Column(name = "was_modified")
    Boolean wasModified;
    @Column(name = "modified_on")
    LocalDateTime modifiedOn;
}
