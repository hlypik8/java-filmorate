package ru.yandex.practicum.filmorate.controllersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User testUser;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testLogin");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldAddUser() {
        User addedUser = userController.post(testUser);
        assertNotNull(addedUser.getId());
        assertEquals(1, addedUser.getId());
    }

    @Test
    void shouldAutoSetNameFromLogin() {
        testUser.setName(null);
        User addedUser = userController.post(testUser);
        assertEquals("testLogin", addedUser.getName());
    }

    @Test
    void shouldUpdateUser() {
        User addedUser = userController.post(testUser);
        addedUser.setEmail("updated@example.com");

        User updatedUser = userController.update(addedUser);
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingUser() {
        testUser.setId(999);
        assertThrows(NotFoundException.class, () -> userController.update(testUser));
    }

    @Test
    void shouldReturnAllUsers() {
        userController.post(testUser);
        Collection<User> users = userController.findAll();
        assertEquals(1, users.size());
    }
}