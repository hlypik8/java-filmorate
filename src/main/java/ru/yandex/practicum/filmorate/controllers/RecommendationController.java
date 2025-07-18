package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable("id") int userId) {
        log.info("Запрос на рекомендации для пользователя с ID: {}", userId);
        Collection<Film> recommendations = recommendationService.getRecommendations(userId);
        log.info("Получено {} рекомендаций", recommendations.size());
        return recommendations;
    }
}
