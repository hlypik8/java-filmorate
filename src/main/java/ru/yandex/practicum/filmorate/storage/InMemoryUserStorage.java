package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    public User getUserById(int id) {
        return users.get(id);
    }

    public Collection<User> usersList() {
        return users.values();
    }

    public User newUser(User user) {
        log.info("Добавление нового пользователя {}", user);
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен! ID: {}", user.getId());
        return user;
    }

    public void removeUser(int id) {
        log.info("Удаление пользователя: {}", users.get(id));
        users.remove(id);
        log.info("Пользователь успешно удален! ID: {}", id);
    }

    public User updateUser(User user) {
        log.info("Обновление данных пользователя ID: {}", user.getId());
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден!");
        }
        User oldUser = users.get(user.getId());
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        log.info("Данные пользователя ID {} успешно обновлены", user.getId());
        log.debug("User = {}", user);
        return oldUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
