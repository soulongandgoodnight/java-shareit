package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    }

    @Test
    void getById_shouldReturnUser() {
        UserDto user = new UserDto();
        user.setName("Tima");
        user.setEmail("tima2@example.com");
        Long id = userService.create(user).getId();

        UserDto found = userService.getById(id);
        assertThat(found.getName()).isEqualTo("Tima");
        assertThat(found.getEmail()).isEqualTo("tima2@example.com");
    }

    @Test
    void update_shouldUpdateUserName() {
        UserDto user = new UserDto();
        user.setName("Old Name");
        user.setEmail("oldname@example.com");
        Long id = userService.create(user).getId();

        UserDto update = new UserDto();
        update.setName("New Name");

        UserDto updated = userService.update(id, update);
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getEmail()).isEqualTo("oldname@example.com");
    }

    @Test
    void delete_shouldRemoveUser() {
        UserDto user = new UserDto();
        user.setName("ToDelete");
        user.setEmail("todelete@example.com");
        Long id = userService.create(user).getId();

        userService.delete(id);

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(NotFoundException.class);
    }
}