package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{id}")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Film>> getRecommendations(@PathVariable int id) {
        log.info("Запрос на рекомендации для пользователя с ID: {}", id);
        List<Film> recommendations = recommendationService.getRecommendations(id);
        if (recommendations.isEmpty()) {
            log.info("Нет рекомендаций для пользователя с ID: {}", id);
        }
        return ResponseEntity.ok(recommendations);
    }

}
