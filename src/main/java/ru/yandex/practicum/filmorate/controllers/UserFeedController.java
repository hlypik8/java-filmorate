package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserFeedService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/users/{id}/feed")
@RequiredArgsConstructor
public class UserFeedController {

    private final UserFeedService userFeedService;

    @GetMapping
    public Collection<Map<String, Object>> getUserFeed(@PathVariable int id) {
        return userFeedService.getUserFeed(id);
    }
}