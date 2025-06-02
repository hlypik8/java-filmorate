package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    public Collection<Film> getFilmsList() {
        return films.values();
    }

    public Film newFilm(Film film) {
        log.info("Добавление нового фильма: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен! ID: {}", film.getId());
        return film;
    }

    public void removeFilm(int id) {
        log.info("Удаление фильма: {}", films.get(id));
        films.remove(id);
        log.info("Фильм успешно удален! ID: {}", id);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма: {}", film.getId());
        log.debug("film = {}", film);
        if (!exists(film.getId())) {
            log.error("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.info("Фильм ID {} успешно обновлен", film.getId());
        log.debug("film = {}", film);
        return oldFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean exists(int id) {
        return films.containsKey(id);
    }
}
