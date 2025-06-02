package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
        log.info("Добавление дружбы между {} и {}", userId, friendId);

        userExistenceCheck(userId);
        userExistenceCheck(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (userStorage.isFriends(userId, friendId)) {
            log.warn("Пользователь {} уже в друзьях у {}", friendId, userId);
            return;
        }

        user.addNewFriend(friendId);
        friend.addNewFriend(userId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление дружбы между пользователями {} и {}", userId, friendId);

        userExistenceCheck(userId);
        userExistenceCheck(friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!userStorage.isFriends(userId, friendId)) {
            log.warn("Пользователь {} не найден в друзьях у {}", friendId, userId);
            return;
        }

        user.removeFriend(friendId);
        friend.removeFriend(userId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    //Здесь заворачиваем мапу в список для тестов Postman
    public List<Map<String, Integer>> getUserFriends(int userId) {
        log.info("Получение списка друзей пользователя {}", userId);

        userExistenceCheck(userId);

        return userStorage.getUserById(userId).getFriends().stream()
                .map(id -> Map.of("id", id))
                .collect(Collectors.toList());
    }

    public List<Map<String, Integer>> getCommonFriends(int userId, int otherUserId) {

        log.info("Получение списка общих друзей пользователя {} и {}", userId, otherUserId);

        userExistenceCheck(userId);
        userExistenceCheck(otherUserId);

        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        userFriends.retainAll(userStorage.getUserById(otherUserId).getFriends());

        return userFriends.stream()
                .map(id -> Map.of("id", id))
                .collect(Collectors.toList());
    }

    private void userExistenceCheck(int userId) {

        if (userStorage.getUserById(userId) == null) {
            log.info("Ошибка! Пользователь с ID: {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
