package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toDto_shouldMapAllFields() {
        User author = new User();
        author.setId(1L);
        author.setName("Автор");
        author.setEmail("author@test.com");

        Item item = new Item();
        item.setId(2L);
        item.setName("Дрель");

        LocalDateTime created = LocalDateTime.of(2025, 3, 1, 12, 0);

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Отличная вещь!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        CommentDto dto = mapper.toDto(comment);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь!");
        assertThat(dto.getAuthorName()).isEqualTo("Автор");
        assertThat(dto.getCreated()).isEqualTo(created);
    }
}