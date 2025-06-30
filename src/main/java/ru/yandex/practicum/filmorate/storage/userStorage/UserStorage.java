package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsersList();

    User newUser(User user);

    void removeUser(int userId);

    User updateUser(User user);

    User getUserById(int userId);

    Collection<User> getCommonFriends(int userId, int otherId);
}
