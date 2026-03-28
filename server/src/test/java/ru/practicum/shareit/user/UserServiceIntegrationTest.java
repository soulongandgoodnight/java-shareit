package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;

    @Test
    void getAll_shouldReturnAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("Tima");
        user1.setEmail("tima@example.com");
        userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("Lexa");
        user2.setEmail("lexa@example.com");
        userService.create(user2);

        List<UserDto> users = userService.getAll();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        assertThat(users).extracting("email")
                .contains("tima@example.com", "lexa@example.com");
    }
}
