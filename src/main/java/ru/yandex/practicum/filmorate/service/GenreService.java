package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> getGenresList() {
        log.info("Запрос списка всех жанров");
        return genreDbStorage.getGenreList();
    }

    public Genre getGenreById(int genreId) {
        log.info("Запрос жанра с id {}", genreId);

        Genre genre = genreDbStorage.getGenreById(genreId);

        if (genre == null) {
            throw new GenreNotFoundException("Такого жанра не существует");
        }

        return genre;
    }
}
