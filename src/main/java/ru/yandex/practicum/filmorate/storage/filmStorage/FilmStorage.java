package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilmsList();

    Film getFilmById(int filmId);

    Film newFilm(Film film);

    void removeFilm(int filmId);

    Film updateFilm(Film film);

    Collection<Film> getPopularFilms(int count, Integer genreId, Integer year);

    Collection<Film> getDirectorsFilmsByLikes(int filmId);

    Collection<Film> getDirectorsFilmsByYear(int filmId);

    Collection<Film> searchFilmsByDirector(String query);

    Collection<Film> searchFilmsByTitle(String query);

    Collection<Film> searchFilmsByDirectorAndTitle(String query);

    Collection<Film> getCommonFilms(int userId, int friendId);
}
