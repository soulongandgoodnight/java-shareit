package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final UserMapper mapper;
    private long idCounter = 1;

    @Override
    public UserDto create(UserDto userDto) {
        validateEmailUnique(userDto.getEmail(), null);
        User user = mapper.toEntity(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return mapper.toDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = getUserEntityById(id);
        validateEmailUnique(userDto.getEmail(), id);
        mapper.updateFromDto(user,userDto);
        return mapper.toDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        return mapper.toDto(getUserEntityById(id));
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public User getUserEntityById(Long id) {
        return users.values().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private void validateEmailUnique(String email, Long excludeId) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        boolean exists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(excludeId));
        if (exists) {
            throw new ConflictException("Email " + email + " уже используется");
        }
    }
}
