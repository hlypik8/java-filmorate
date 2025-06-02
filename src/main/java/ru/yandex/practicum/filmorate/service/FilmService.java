package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);

        filmExistenceCheck(filmId);
        userExistenceCheck(userId);

        filmStorage.getFilmById(filmId).addLike(userId);
        log.info("Лайк фильму {} от пользователя {} добавлен", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);

        filmExistenceCheck(filmId);
        userExistenceCheck(userId);

        filmStorage.getFilmById(filmId).removeLike(userId);
        log.info("Лайк фильму {} от пользователя {} удален", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос {} популярных фильмов", count);
        return filmStorage.getFilmsList().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void filmExistenceCheck(int filmId) {

        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    public void userExistenceCheck(int userId) {

        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
