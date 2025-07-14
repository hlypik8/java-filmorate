package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesDbStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LikesDbStorage likesDbStorage;
    private final FilmService filmService;

    public List<Film> getRecommendations(int userId) {
        log.info("Получаем рекомендации для пользователя с ID: {}", userId);

        Set<Integer> likedFilms = new HashSet<>(likesDbStorage.getLikesByUser(userId));
        log.info("Лайки пользователя {}: {}", userId, likedFilms);

        Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();

        for (Integer filmId : likedFilms) {
            List<Integer> usersWhoLikedFilm = likesDbStorage.getUsersWhoLikedFilm(filmId);
            log.info("Пользователи, которые поставили лайк фильму {}: {}", filmId, usersWhoLikedFilm);
            for (Integer userWhoLiked : usersWhoLikedFilm) {
                if (userWhoLiked != userId) {
                    Set<Integer> otherUserLikes = new HashSet<>(likesDbStorage.getLikesByUser(userWhoLiked));
                    for (Integer otherFilmId : otherUserLikes) {
                        if (!likedFilms.contains(otherFilmId)) {
                            double observedDiff = 1;
                            diff.computeIfAbsent(filmId, k -> new HashMap<>())
                                    .put(otherFilmId, diff.get(filmId).getOrDefault(otherFilmId, 0.0) + observedDiff);
                            freq.computeIfAbsent(filmId, k -> new HashMap<>())
                                    .put(otherFilmId, freq.get(filmId).getOrDefault(otherFilmId, 0) + 1);
                        }
                    }
                }
            }
        }

        List<Film> recommendedFilms = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Double>> entry : diff.entrySet()) {
            for (Map.Entry<Integer, Double> innerEntry : entry.getValue().entrySet()) {
                double predictedValue = innerEntry.getValue() / freq.get(entry.getKey()).get(innerEntry.getKey());
                if (predictedValue > 0) {
                    Film film = filmService.getFilmById(innerEntry.getKey());
                    if (!likedFilms.contains(film.getId()) && !recommendedFilms.contains(film)) {
                        recommendedFilms.add(film);
                    }
                }
            }
        }

        log.info("Рекомендованные фильмы для пользователя {}: {}", userId, recommendedFilms);
        return recommendedFilms;
    }

}


