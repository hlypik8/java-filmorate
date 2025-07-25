package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidRequestFormat;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorDbStorage;
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
    private final DirectorDbStorage directorDbStorage;
    private final EventService eventService;

    public Collection<Film> getFilmsList() {
        log.info("Запрос списка всех фильмов");
        return filmStorage.getFilmsList();
    }

    public Collection<Film> getFilmByDirector(int directorId, String sortBy) {
        log.info("Получение фильмов режиссера с id {} по кол-ву лайков", directorId);
        if (sortBy.equals("likes")) {
            Collection<Film> films = filmStorage.getDirectorsFilmsByLikes(directorId);
            if (films.isEmpty()) {
                throw new NotFoundException("Фильмы режиссера не найдены");
            }
            return films;
        }
        if (sortBy.equals("year")) {
            Collection<Film> films = filmStorage.getDirectorsFilmsByYear(directorId);
            if (films.isEmpty()) {
                throw new NotFoundException("Фильмы режиссера не найдены");
            }
            return films;
        }

        return Collections.emptyList();
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
        filmStorage.newFilm(film);
        return getFilmById(film.getId());
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма {}", film.getId());

        validateMpa(film.getMpa());
        validateGenre(film.getGenres());
        log.debug("Валидация mpa и жанров пройдена");

        log.info("Фильм успешно обновлен {}", film);
        filmStorage.updateFilm(film);
        return getFilmById(film.getId());
    }

    public void removeFilm(int filmId) {
        log.info("Удаление фильма с id {}", filmId);
        filmStorage.removeFilm(filmId);
        log.info("Фильм с id {} успешно удален", filmId);
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

        eventService.createAddLikeEvent(userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);

        likesDbStorage.removeLike(userId, filmId);

        log.info("Лайк фильму {} от пользователя {} удален", filmId, userId);

        eventService.createRemoveLikeEvent(userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        log.info("Запрос {} популярных фильмов", count);

        return filmStorage.getPopularFilms(count, genreId, year).stream().toList();
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        log.info("Запрос общих фильмов пользователя {} и {}", userId, friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public Collection<Film> searchFilms(String query, List<String> by) {
        if (by.size() == 1 && by.contains("title")) {
            return filmStorage.searchFilmsByTitle(query);
        }
        if (by.size() == 1 && by.contains("director")) {
            return filmStorage.searchFilmsByDirector(query);
        }
        if (by.contains("title") && by.contains("director")) {
            return filmStorage.searchFilmsByDirectorAndTitle(query);
        }
        throw new InvalidRequestFormat("Поддерживаются только значения 'title' и 'director'");
    }
}