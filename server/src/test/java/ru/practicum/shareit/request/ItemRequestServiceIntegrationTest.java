package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final ItemRequestService requestService;
    private final UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        user.setName("Requester");
        user.setEmail("requester@example.com");
        userId = userService.create(user).getId();

        ItemRequestCreateDto dto1 = new ItemRequestCreateDto();
        dto1.setDescription("Нужна дрель");
        requestService.create(userId, dto1);

        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Нужен молоток");
        requestService.create(userId, dto2);
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() {
        List<ItemRequestDto> requests = requestService.getAllByUser(userId);

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting("description")
                .containsExactlyInAnyOrder("Нужна дрель", "Нужен молоток");
    }
}
