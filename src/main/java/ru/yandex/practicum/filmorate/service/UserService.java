package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getUsersList(){
        return userStorage.getUsersList();
    }

    public User newUser(User user){
        return userStorage.newUser(user);
    }

    public User updateUser(User user){
        return userStorage.updateUser(user);
    }

/*
    public void addFriend(int userId, int friendId) {
        log.info("Добавление дружбы между {} и {}", userId, friendId);

        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

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

        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

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

        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        return userStorage.getUserById(userId).getFriends().stream()
                .map(id -> Map.of("id", id))
                .collect(Collectors.toList());
    }

    public List<Map<String, Integer>> getCommonFriends(int userId, int otherUserId) {

        log.info("Получение списка общих друзей пользователя {} и {}", userId, otherUserId);

        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!userExists(otherUserId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        userFriends.retainAll(userStorage.getUserById(otherUserId).getFriends());

        return userFriends.stream()
                .map(id -> Map.of("id", id))
                .collect(Collectors.toList());
    }

    private boolean userExists(int userId) {
        return userStorage.getUserById(userId) != null;
    }

 */
}
