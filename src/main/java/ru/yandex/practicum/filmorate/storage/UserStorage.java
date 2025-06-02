package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    boolean isFriends(int userId, int friendId);

    Collection<User> usersList();

    User newUser(User user);

    void removeUser(int id);

    User updateUser(User user);

    User getUserById(int user);
}
