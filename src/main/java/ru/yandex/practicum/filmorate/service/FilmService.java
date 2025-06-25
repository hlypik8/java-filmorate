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
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public Collection<Film> getFilmsList(){
        return filmStorage.getFilmsList();
    }

    public Film newFilm(Film film){
        validateMpa(film.getMpa());
        validateGenre(film.getGenres());

        Mpa mpa = mpaDbStorage.getMpaById(film.getMpa().getId());
        Set<Genre> realGenres = film.getGenres().stream()
                .map(genre -> genreDbStorage.getGenreById(genre.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        film.setGenres(realGenres);
        film.setMpa(mpa);

        return filmStorage.newFilm(film);
    }

    public Film updateFilm(Film film){
        return filmStorage.updateFilm(film);
    }

    private void validateMpa(Mpa mpa){
        if(mpaDbStorage.getMpaById(mpa.getId()) == null){
            throw new MpaNotFoundException("Неверный рейтинг MPA");
        }
    }

    private void validateGenre(Set<Genre> genres){
        for (Genre genre : genres){
            if(genreDbStorage.getGenreById(genre.getId()) == null){
                throw new GenreNotFoundException("Жанр не найден");
            }
        }
    }


//    public void addLike(int filmId, int userId) {
//        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);
//
//        if (!filmExists(filmId)) {
//            throw new NotFoundException("Фильм с id " + filmId + " не найден");
//        }
//        if (!userExists(userId)) {
//            throw new NotFoundException("Пользователь с id " + userId + " не найден");
//        }
//
//        filmStorage.getFilmById(filmId).addLike(userId);
//        log.info("Лайк фильму {} от пользователя {} добавлен", filmId, userId);
//    }

//    public void deleteLike(int filmId, int userId) {
//        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);
//
//        if (!filmExists(filmId)) {
//            throw new NotFoundException("Фильм с id " + filmId + " не найден");
//        }
//        if (!userExists(userId)) {
//            throw new NotFoundException("Пользователь с id " + userId + " не найден");
//        }
//        filmStorage.getFilmById(filmId).removeLike(userId);
//        log.info("Лайк фильму {} от пользователя {} удален", filmId, userId);
//    }

//    public List<Film> getPopularFilms(int count) {
//        log.info("Запрос {} популярных фильмов", count);
//        return filmStorage.getFilmsList().stream()
//                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
//                .limit(count)
//                .collect(Collectors.toList());
//    }

//    public boolean filmExists(int filmId) {
//        return filmStorage.getFilmById(filmId) != null;
//    }

//    public boolean userExists(int userId) {
//        return userStorage.getUserById(userId) != null;
//    }
}


