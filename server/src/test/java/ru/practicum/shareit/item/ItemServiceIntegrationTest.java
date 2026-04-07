package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private Long userId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userId = userService.create(user).getId();

        ItemRequestDto item1 = new ItemRequestDto();
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);
        itemId = itemService.create(item1, userId).getId();

        ItemRequestDto item2 = new ItemRequestDto();
        item2.setName("Молоток");
        item2.setDescription("Обычный молоток");
        item2.setAvailable(true);
        itemService.create(item2, userId);
    }

    @Test
    void getAllByOwner_shouldReturnAllOwnerItems() {
        List<ItemDto> items = itemService.getAllByOwner(userId);
        assertThat(items).hasSize(2);
        assertThat(items).extracting("name")
                .containsExactlyInAnyOrder("Дрель", "Молоток");
    }

    @Test
    void getById_shouldReturnItem() {
        ItemDto item = itemService.getById(itemId, userId);
        assertThat(item.getName()).isEqualTo("Дрель");
    }

    @Test
    void search_shouldReturnMatchingItems() {
        List<ItemDto> items = itemService.search("дрель");
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void search_withEmptyText_shouldReturnEmpty() {
        List<ItemDto> items = itemService.search("");
        assertThat(items).isEmpty();
    }

    @Test
    void update_shouldUpdateItemFields() {
        ItemRequestDto update = new ItemRequestDto();
        update.setName("Дрель обновлённая");
        update.setDescription("Очень мощная");
        update.setAvailable(false);

        ItemDto updated = itemService.update(itemId, update, userId);

        assertThat(updated.getName()).isEqualTo("Дрель обновлённая");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void createComment_shouldCreateComment() {
        var dto = new CommentRequestDto();
        var commentText = "Хорошая доставка";
        dto.setText(commentText);
        assertThatThrownBy(() -> itemService.createComment(itemId, userId, dto))
                .isInstanceOf(ValidationException.class);
    }
}