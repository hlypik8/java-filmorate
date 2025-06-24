package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Collection<Film>> getFilmsList();

    Optional<Film> getFilmById(int filmId);

    Optional<Film> newFilm(Film film);

    void removeFilm(int id);

    Optional<Film> updateFilm(Film film);
}
