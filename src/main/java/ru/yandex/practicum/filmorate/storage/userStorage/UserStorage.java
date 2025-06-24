package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> usersList();

    User newUser(User user);

    void removeUser(int userId);

    User updateUser(User user);

    User getUserById(int userId);
}
