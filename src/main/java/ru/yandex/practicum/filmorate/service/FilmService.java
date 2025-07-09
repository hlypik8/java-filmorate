package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikesDbStorage likesDbStorage;

    public Collection<Film> getFilmsList() {
        log.info("Запрос списка всех фильмов");
        return filmStorage.getFilmsList();
    }

    public Film getFilmById(int filmId) {
        log.info("Получение фильма с id {}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film newFilm(Film film) {
        log.info("Добавление нового фильма");

        validateMpa(film.getMpa());
        validateGenre(film.getGenres());

        log.debug("Валидация mpa и жанров пройдена");
        log.info("Новый фильм успешно добавлен {}", film.getName());
        return filmStorage.newFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма {}", film.getId());

        validateMpa(film.getMpa());
        validateGenre(film.getGenres());
        log.debug("Валидация mpa и жанров пройдена");

        log.info("Фильм успешно обновлен {}", film);
        return filmStorage.updateFilm(film);
    }

    private void validateMpa(Mpa mpa) {
        if (mpaDbStorage.getMpaById(mpa.getId()) == null) {
            throw new MpaNotFoundException("Неверный рейтинг MPA");
        }
    }

    private void validateGenre(Set<Genre> genres) {
        Set<Integer> allGenreIds = genreDbStorage.getGenreList().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        for (Genre genre : genres) {
            if (!allGenreIds.contains(genre.getId())) {
                throw new GenreNotFoundException("Жанр не найден: " + genre.getId());
            }
        }
    }

    public void addLike(int userId, int filmId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);

        likesDbStorage.addLike(userId, filmId);

        log.info("Лайк фильму {} от пользователя {} добавлен", filmId, userId);
    }

    public void deleteLike(int userId, int filmId) {
        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);

        likesDbStorage.removeLike(userId, filmId);

        log.info("Лайк фильму {} от пользователя {} удален", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос {} популярных фильмов", count);

        return filmStorage.getPopularFilms(count).stream().toList();
    }

    public List<Film> getRecommendations(int userId) {
        Set<Integer> likedFilms = new HashSet<>(likesDbStorage.getLikesByUser(userId));

        Map<Integer, Integer> similarUsers = getUsersWithSimilarLikes(userId, likedFilms);

        List<Film> recommendedFilms = new ArrayList<>();
        for (Integer similarUserId : similarUsers.keySet()) {
            Set<Integer> filmsLikedBySimilarUser = new HashSet<>(likesDbStorage.getLikesByUser(similarUserId));

            for (Integer filmId : filmsLikedBySimilarUser) {
                if (!likedFilms.contains(filmId)) {
                    Film film = getFilmById(filmId);
                    recommendedFilms.add(film);
                }
            }
        }

        return recommendedFilms;
    }

    private Set<Integer> getLikedFilmsByUser(int userId) {
        return new HashSet<>(likesDbStorage.getLikesByUser(userId));
    }

    private Map<Integer, Integer> getUsersWithSimilarLikes(int userId, Set<Integer> likedFilms) {
        Map<Integer, Integer> similarUsers = new HashMap<>();

        for (Integer filmId : likedFilms) {
            List<Integer> usersWhoLikedFilm = likesDbStorage.getUsersWhoLikedFilm(filmId);

            for (Integer similarUserId : usersWhoLikedFilm) {
                if (similarUserId != userId) {
                    similarUsers.put(similarUserId, similarUsers.getOrDefault(similarUserId, 0) + 1);
                }
            }
        }

        return similarUsers.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))  // Сортировка по количеству пересечений
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}


