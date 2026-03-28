package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        UserDto savedUser = userService.create(user);
        userId = savedUser.getId();

        ItemRequestDto item1 = new ItemRequestDto();
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);
        itemService.create(item1, userId);

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
}
