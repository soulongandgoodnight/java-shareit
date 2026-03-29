package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toDto_shouldMapAllFields() {
        User requestor = new User();
        requestor.setId(1L);
        requestor.setName("Lexa");
        requestor.setEmail("lexa@test.com");

        LocalDateTime created = LocalDateTime.of(2025, 5, 1, 10, 0);

        ItemRequest request = new ItemRequest();
        request.setId(7L);
        request.setDescription("Нужна дрель");
        request.setRequestor(requestor);
        request.setCreated(created);

        ItemRequestDto dto = mapper.toDto(request);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).isEmpty();
    }
}