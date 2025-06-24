/*package ru.yandex.practicum.filmorate.controllersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private FilmController filmController;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setDuration(120);
    }

    @Test
    void shouldAddFilm() {
        Film addedFilm = filmController.post(testFilm);
        assertNotNull(addedFilm.getId());
        assertEquals(1, addedFilm.getId());
    }

    @Test
    void shouldGetAllFilms() {
        filmController.post(testFilm);
        Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size());
    }

    @Test
    void shouldUpdateFilm() {
        Film addedFilm = filmController.post(testFilm);
        addedFilm.setName("Updated Name");

        Film updatedFilm = filmController.update(addedFilm);
        assertEquals("Updated Name", updatedFilm.getName());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingFilm() {
        testFilm.setId(999);
        assertThrows(NotFoundException.class, () -> filmController.update(testFilm));
    }

    @Test
    void shouldGenerateSequentialIds() {

        Film firstFilm = new Film();
        firstFilm.setName("First Film");
        firstFilm.setDescription("First Description");
        firstFilm.setReleaseDate(LocalDate.now());
        firstFilm.setDuration(90);

        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setDescription("Second Description");
        secondFilm.setReleaseDate(LocalDate.now());
        secondFilm.setDuration(120);

        Film first = filmController.post(firstFilm);
        Film second = filmController.post(secondFilm);

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }
}

 */
