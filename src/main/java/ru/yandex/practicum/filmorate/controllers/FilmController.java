package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getFilmsList();
    }

    @GetMapping("/{id}")
    public Film findOne(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilmsSorted(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmByDirector(directorId, sortBy);
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        return filmService.newFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(userId, id);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.removeFilm(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10", name = "count") int count,
                                       @RequestParam(required = false, name = "genreId") Integer genreId,
                                       @RequestParam(required = false, name = "year") Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam(name = "userId") int userId,
                                           @RequestParam(name = "friendId") int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.searchFilms(query, by);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(NotFoundException e) {
        return Map.of("error", "фильм не найден",
                "errorMessage", e.getMessage());
    }
}


