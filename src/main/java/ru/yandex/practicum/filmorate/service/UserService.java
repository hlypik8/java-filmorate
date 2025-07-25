package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;
    private final FriendsDbStorage friendsStorage;
    private final EventService eventService;

    public Collection<User> getUsersList() {
        log.info("Запрос списка всех пользователей");
        return userStorage.getUsersList();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public User newUser(User user) {
        log.info("Создание нового пользователя {}", user);
        return userStorage.newUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление данных пользователя с id {}", user.getId());
        return userStorage.updateUser(user);
    }

    public void removeUser(int userId) {
        log.info("Удаление пользователя с id {}", userId);
        userStorage.removeUser(userId);
        log.info("Пользователь с id {} успешно удален", userId);
    }

    public void addFriend(int userId, int friendId) {
        log.info("Добавление дружбы между {} и {}", userId, friendId);
        validateUser(userId);
        validateUser(friendId);

        friendsStorage.addFriend(userId, friendId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
        eventService.createAddFriendEvent(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление дружбы между пользователями {} и {}", userId, friendId);
        validateUser(userId);
        validateUser(friendId);

        friendsStorage.deleteFriend(userId, friendId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
        eventService.createRemoveFriendEvent(userId, friendId);
    }

    public Collection<User> getUserFriends(int userId) {
        log.info("Запрос на получение друзей пользователя с id {}", userId);
        validateUser(userId);

        return friendsStorage.getFriendIds(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        log.info("Запрос на получение общих друзей пользователя {} и {}", userId, otherId);
        validateUser(userId);
        validateUser(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }

    private void validateUser(int userId) {
        if (!userStorage.exists(userId)) {
            log.warn("Пользователь c id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public Collection<Event> getFeed(int userId) {
        validateUser(userId);
        return eventService.getFeed(userId);
    }
}
