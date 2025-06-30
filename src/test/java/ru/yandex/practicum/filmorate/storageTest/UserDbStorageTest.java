package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Rollback(false)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void clearAndSetup() {
        // Сначала чистим дочерние таблицы
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM likes");
        // Затем самих пользователей
        jdbcTemplate.update("DELETE FROM users");
        // Сбрасываем IDENTITY
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        // Инициализируем «эталонного» пользователя
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUser() {
        User created = userStorage.newUser(user);
        assertEquals(user.getEmail(), created.getEmail());
        assertEquals(user.getLogin(), created.getLogin());
        assertEquals(user.getName(), created.getName());
        assertEquals(user.getBirthday(), created.getBirthday());
    }

    @Test
    void testUpdateUser() {
        User created = userStorage.newUser(user);
        created.setName("Updated Name");
        created.setLogin("updatedLogin");

        User updated = userStorage.updateUser(created);
        assertEquals("Updated Name", updated.getName());
        assertEquals("updatedLogin", updated.getLogin());
    }

    @Test
    void testGetAllUsers() {
        userStorage.newUser(user);

        User anotherUser = new User();
        anotherUser.setEmail("other@example.com");
        anotherUser.setLogin("otherLogin");
        anotherUser.setName("Other User");
        anotherUser.setBirthday(LocalDate.of(1985, 5, 5));
        userStorage.newUser(anotherUser);

        Collection<User> users = userStorage.getUsersList();
        assertTrue(users.size() >= 2);
    }
}
