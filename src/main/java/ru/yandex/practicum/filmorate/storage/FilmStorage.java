package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilmsList();

    Film getFilmById(int filmId);

    Film newFilm(Film film);

    void removeFilm(int id);

    Film updateFilm(Film film);
}
