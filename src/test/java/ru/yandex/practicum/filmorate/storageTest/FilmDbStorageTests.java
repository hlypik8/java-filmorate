package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friendsStorage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likesStorage.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.*;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class,
        LikesDbStorage.class,
        UserDbStorage.class, UserRowMapper.class,
        FriendsDbStorage.class})
public class FilmDbStorageTests {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    private Film film;

    @BeforeEach
    void setup() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpaDbStorage.getMpaById(1));
        film.setGenres(Set.of(genreDbStorage.getGenreById(1)));
    }

    @Test
    void testCreateAndGetById() {
        Film created = filmDbStorage.newFilm(film);
        assertNotNull(created);
        assertTrue(created.getId() > 0);

        Film result = filmDbStorage.getFilmById(created.getId());
        assertEquals(film.getName(), result.getName());
        assertEquals(film.getDescription(), result.getDescription());
        assertEquals(film.getDuration(), result.getDuration());
        assertEquals(film.getReleaseDate(), result.getReleaseDate());
        assertEquals(film.getMpa().getId(), result.getMpa().getId());
        assertTrue(result.getGenres().containsAll(film.getGenres()));
    }

    @Test
    void testUpdateFilm() {
        Film created = filmDbStorage.newFilm(film);
        created.setName("Updated name");
        created.setDescription("Updated description");
        created.setDuration(90);
        created.setReleaseDate(LocalDate.of(2010, 10, 10));
        created.setMpa(mpaDbStorage.getMpaById(2));
        created.setGenres(Set.of(genreDbStorage.getGenreById(2)));

        Film updated = filmDbStorage.updateFilm(created);

        assertAll("Проверка полей обновлённого фильма",
                () -> assertEquals("Updated name", updated.getName(), "Имя не обновилось"),
                () -> assertEquals("Updated description", updated.getDescription(), "Описание не обновилось"),
                () -> assertEquals(90, updated.getDuration(), "Длительность не обновилась"),
                () -> assertEquals(LocalDate.of(2010, 10, 10), updated.getReleaseDate(), "Дата релиза не обновилась"),
                () -> assertEquals(2, updated.getMpa().getId(), "MPA-рейтинг не обновился"),
                () -> assertTrue(updated.getGenres().contains(genreDbStorage.getGenreById(2)),
                        "Жанры не обновились")
        );
    }

    @Test
    void testGetAllFilms() {
        filmDbStorage.newFilm(film);
        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Another description");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(100);
        film2.setMpa(mpaDbStorage.getMpaById(1));
        film2.setGenres(Set.of(genreDbStorage.getGenreById(1)));
        filmDbStorage.newFilm(film2);

        List<Film> films = (List<Film>) filmDbStorage.getFilmsList();
        assertTrue(films.size() >= 2);
    }

    @Test
    void testRemoveFilm() {
        Film created = filmDbStorage.newFilm(film);

        filmDbStorage.removeFilm(created.getId());

        assertThrows(NotFoundException.class, () -> filmDbStorage.getFilmById(created.getId()));
    }
}