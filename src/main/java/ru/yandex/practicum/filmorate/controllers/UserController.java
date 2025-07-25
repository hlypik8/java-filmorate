package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getUsersList();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User post(@Valid @RequestBody User user) {
        return userService.newUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        userService.removeUser(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getFeed(@PathVariable int id) {
        return userService.getFeed(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(NotFoundException e) {
        return Map.of("error", "пользователь не найден",
                "errorMessage", e.getMessage());
    }
}
