package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendsDbStorage friendsStorage;

    public Collection<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User newUser(User user) {
        return userStorage.newUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }


    public void addFriend(int userId, int friendId) {
        log.info("Добавление дружбы между {} и {}", userId, friendId);

        if (!userExists(userId) || !userExists(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        friendsStorage.addFriend(userId, friendId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление дружбы между пользователями {} и {}", userId, friendId);

        if (!userExists(userId) || !userExists(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        friendsStorage.deleteFriend(userId, friendId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getUserFriends(int userId) {
        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return friendsStorage.getFriendIds(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        if (!userExists(userId) || !userExists(otherId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return friendsStorage.getCommonFriendIds(userId, otherId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    private boolean userExists(int userId) {
        return userStorage.getUserById(userId) != null;
    }

}
