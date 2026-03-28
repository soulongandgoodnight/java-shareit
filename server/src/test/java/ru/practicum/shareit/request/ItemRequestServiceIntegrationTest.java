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
    private Long otherUserId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        user.setName("Requester");
        user.setEmail("requester@example.com");
        userId = userService.create(user).getId();

        UserDto other = new UserDto();
        other.setName("Other");
        other.setEmail("other@example.com");
        otherUserId = userService.create(other).getId();

        ItemRequestCreateDto dto1 = new ItemRequestCreateDto();
        dto1.setDescription("Нужна дрель");
        requestId = requestService.create(userId, dto1).getId();

        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Нужен молоток");
        requestService.create(userId, dto2);

        ItemRequestCreateDto dto3 = new ItemRequestCreateDto();
        dto3.setDescription("Нужна пила");
        requestService.create(otherUserId, dto3);
    }

    @Test
    void getAllByUser_shouldReturnOnlyUserRequests() {
        List<ItemRequestDto> requests = requestService.getAllByUser(userId);
        assertThat(requests).hasSize(2);
        assertThat(requests).extracting("description")
                .containsExactlyInAnyOrder("Нужна дрель", "Нужен молоток");
    }

    @Test
    void getAllOthers_shouldReturnOtherUsersRequests() {
        List<ItemRequestDto> requests = requestService.getAllOthers(userId);
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Нужна пила");
    }

    @Test
    void getById_shouldReturnRequest() {
        ItemRequestDto request = requestService.getById(requestId, userId);
        assertThat(request.getId()).isEqualTo(requestId);
        assertThat(request.getDescription()).isEqualTo("Нужна дрель");
    }

    @Test
    void create_shouldReturnRequestWithCreatedDate() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен шуруповёрт");

        ItemRequestDto created = requestService.create(userId, dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужен шуруповёрт");
        assertThat(created.getCreated()).isNotNull();
    }
}