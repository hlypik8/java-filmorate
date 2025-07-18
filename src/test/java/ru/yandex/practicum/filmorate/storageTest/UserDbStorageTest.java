package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase()
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    private void clearDatabase() {
        jdbcTemplate.update("DELETE FROM likes");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
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
        // Создаём нового пользователя
        User newUser = new User();
        newUser.setEmail("user@example.com");
        newUser.setLogin("userLogin");
        newUser.setName("User Name");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        // Сохраняем в БД
        User created = userStorage.newUser(newUser);

        // Обновляем данные
        created.setName("Updated Name");
        created.setLogin("updatedLogin");

        // Обновляем пользователя в БД
        User updated = userStorage.updateUser(created);

        // Проверяем, что обновление прошло корректно
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
