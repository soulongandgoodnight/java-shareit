package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void toDto_shouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(5L);
        item.setName("Молоток");
        item.setDescription("Обычный молоток");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = mapper.toDto(item);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Молоток");
        assertThat(dto.getDescription()).isEqualTo("Обычный молоток");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        ItemRequestDto request = new ItemRequestDto();
        request.setName("Пила");
        request.setDescription("Острая пила");
        request.setAvailable(false);
        request.setRequestId(3L);

        Item item = mapper.toEntity(request);

        assertThat(item.getName()).isEqualTo("Пила");
        assertThat(item.getDescription()).isEqualTo("Острая пила");
        assertThat(item.getAvailable()).isFalse();
        assertThat(item.getRequestId()).isEqualTo(3L);
    }

    @Test
    void toEntity_withoutRequestId_shouldNotSetRequestId() {
        ItemRequestDto request = new ItemRequestDto();
        request.setName("Пила");
        request.setDescription("Острая пила");
        request.setAvailable(true);

        Item item = mapper.toEntity(request);

        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void updateFromDto_shouldUpdateOnlyNonNullFields() {
        Item item = new Item();
        item.setName("Старое имя");
        item.setDescription("Старое описание");
        item.setAvailable(true);

        ItemRequestDto update = new ItemRequestDto();
        update.setName("Новое имя");

        mapper.updateFromDto(item, update);

        assertThat(item.getName()).isEqualTo("Новое имя");
        assertThat(item.getDescription()).isEqualTo("Старое описание");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void updateFromDto_shouldUpdateAllFields() {
        Item item = new Item();
        item.setName("Старое");
        item.setDescription("Старое описание");
        item.setAvailable(true);

        ItemRequestDto update = new ItemRequestDto();
        update.setName("Новое");
        update.setDescription("Новое описание");
        update.setAvailable(false);

        mapper.updateFromDto(item, update);

        assertThat(item.getName()).isEqualTo("Новое");
        assertThat(item.getDescription()).isEqualTo("Новое описание");
        assertThat(item.getAvailable()).isFalse();
    }
}