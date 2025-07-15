package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LikesDbStorage likesDbStorage;
    private final FilmService filmService;

    public List<Film> getRecommendations(int userId) {
        log.info("Получаем рекомендации для пользователя с ID: {}", userId);

        Collection<Integer> likedFilms = likesDbStorage.getLikesByUser(userId);
        log.info("Лайки пользователя {}: {}", userId, likedFilms);

        Set<Integer> likedFilmSet = new HashSet<>(likedFilms);

        Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();

        likedFilmSet.forEach(filmId -> {
            List<Integer> usersWhoLikedFilm = likesDbStorage.getUsersWhoLikedFilm(filmId)
                    .stream()
                    .toList();
            log.info("Пользователи, которые поставили лайк фильму {}: {}", filmId, usersWhoLikedFilm);

            usersWhoLikedFilm.stream()
                    .filter(userWhoLiked -> userWhoLiked != userId)
                    .forEach(userWhoLiked -> {
                        Collection<Integer> otherUserLikes = likesDbStorage.getLikesByUser(userWhoLiked);
                        otherUserLikes.stream()
                                .filter(otherFilmId -> !likedFilmSet.contains(otherFilmId))
                                .forEach(otherFilmId -> {
                                    double observedDiff = 1;
                                    diff.computeIfAbsent(filmId, k -> new HashMap<>())
                                            .merge(otherFilmId, observedDiff, Double::sum);
                                    freq.computeIfAbsent(filmId, k -> new HashMap<>())
                                            .merge(otherFilmId, 1, Integer::sum);
                                });
                    });
        });

        List<Film> recommendedFilms = diff.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(innerEntry -> new AbstractMap.SimpleEntry<>(entry.getKey(), innerEntry)))
                .map(entry -> {
                    double predictedValue = entry.getValue().getValue() / freq.get(entry.getKey()).get(entry.getValue().getKey());
                    if (predictedValue > 0) {
                        Film film = filmService.getFilmById(entry.getValue().getKey());
                        if (!likedFilmSet.contains(film.getId())) {
                            return film;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        log.info("Рекомендованные фильмы для пользователя {}: {}", userId, recommendedFilms);
        return recommendedFilms;
    }
}



