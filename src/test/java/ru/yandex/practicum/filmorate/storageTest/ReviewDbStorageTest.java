package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewDbStorageTest {
    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private Review testReview;
    private User testUser;
    private Film testFilm;
    private Mpa testMpa;

    @BeforeEach
    void setUp() {
        // Очистка всех связанных таблиц в правильном порядке
        jdbcTemplate.update("DELETE FROM review_ratings");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM films_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM mpa_ratings");

        // Сброс sequence для всех таблиц
        jdbcTemplate.execute("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE mpa_ratings ALTER COLUMN id RESTART WITH 1");

        // Создание тестового MPA через прямой SQL-запрос
        jdbcTemplate.update(
                "INSERT INTO mpa_ratings (id, name, description) VALUES (1, 'G', 'Нет возрастных ограничений')");
        testMpa = new Mpa();
        testMpa.setId(1);
        testMpa.setName("G");
        testMpa.setDescription("Нет возрастных ограничений");

        // Создание тестового пользователя через UserDbStorage
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testLogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser = userStorage.newUser(testUser);

        // Создание тестового фильма через FilmDbStorage
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(testMpa);
        testFilm = filmStorage.newFilm(testFilm);

        // Создание тестового отзыва
        testReview = new Review();
        testReview.setContent("Great film!");
        testReview.setIsPositive(true);
        testReview.setUserId(testUser.getId());
        testReview.setFilmId(testFilm.getId());
    }

    @Test
    void shouldCreateAndRetrieveReview() {
        Review created = reviewStorage.addReview(testReview);
        assertNotNull(created.getReviewId(), "ID отзыва не должен быть null");
        assertEquals(0, created.getUseful(), "Рейтинг полезности по умолчанию должен быть 0");

        Review found = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(created.getContent(), found.getContent(), "Содержимое отзыва должно совпадать");
        assertEquals(testUser.getId(), found.getUserId(), "ID пользователя должно совпадать");
        assertEquals(testFilm.getId(), found.getFilmId(), "ID фильма должно совпадать");
    }

    @Test
    void shouldUpdateReview() {
        Review created = reviewStorage.addReview(testReview);
        created.setContent("Updated content");
        created.setIsPositive(false);

        Review updated = reviewStorage.updateReview(created);
        assertEquals(created.getReviewId(), updated.getReviewId(), "ID отзыва должно остаться прежним");
        assertEquals("Updated content", updated.getContent(), "Содержимое должно обновиться");
        assertFalse(updated.getIsPositive(), "Тип отзыва должен измениться на негативный");
    }

    @Test
    void shouldThrowWhenReviewNotFound() {
        assertThrows(NotFoundException.class, () -> reviewStorage.getReviewById(999),
                "Должно выбрасываться исключение при поиске несуществующего отзыва");
    }

    @Test
    void shouldDeleteReview() {
        Review created = reviewStorage.addReview(testReview);
        reviewStorage.deleteReview(created.getReviewId());

        assertThrows(NotFoundException.class, () -> reviewStorage.getReviewById(created.getReviewId()),
                "Отзыв должен удаляться из базы данных");
    }

    @Test
    void shouldGetReviewsByFilmId() {
        Review created = reviewStorage.addReview(testReview);

        // Создаем второй фильм и отзыв
        jdbcTemplate.update(
                "INSERT INTO films (id, name, description, release_date, duration, mpa_rating_id) " +
                        "VALUES (2, 'Another Film', 'Desc', '2001-01-01', 90, 1)");

        Review anotherReview = new Review();
        anotherReview.setContent("Another review");
        anotherReview.setIsPositive(false);
        anotherReview.setUserId(testUser.getId());
        anotherReview.setFilmId(2); // ID второго фильма
        reviewStorage.addReview(anotherReview);

        List<Review> reviews = reviewStorage.getReviewsByFilmId(testFilm.getId(), 10);
        assertEquals(1, reviews.size(), "Должен вернуться только один отзыв для указанного фильма");
        assertEquals(created.getReviewId(), reviews.get(0).getReviewId(), "ID отзыва должно совпадать");
    }

    @Test
    void shouldManageReviewRatings() {
        // Создаем второго пользователя через SQL
        jdbcTemplate.update(
                "INSERT INTO users (id, email, login, name, birthday) " +
                        "VALUES (2, 'user2@test', 'user2', 'User 2', '1995-05-05')");

        Review created = reviewStorage.addReview(testReview);

        // Добавляем лайк от первого пользователя
        reviewStorage.addRating(created.getReviewId(), testUser.getId(), true);
        Review afterLike = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(1, afterLike.getUseful(), "Рейтинг должен увеличиться на 1 после лайка");

        // Добавляем дизлайк от второго пользователя
        reviewStorage.addRating(created.getReviewId(), 2, false);
        Review afterDislike = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(0, afterDislike.getUseful(), "Рейтинг должен быть 0 (1 лайк + 1 дизлайк)");

        // Удаляем лайк
        reviewStorage.removeRating(created.getReviewId(), testUser.getId());
        Review afterRemove = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(-1, afterRemove.getUseful(), "Рейтинг должен быть -1 после удаления лайка");
    }

    @Test
    void shouldGetAllReviewsWhenFilmIdNotSpecified() {
        reviewStorage.addReview(testReview);

        // Создаем второй отзыв
        Review anotherReview = new Review();
        anotherReview.setContent("Another review");
        anotherReview.setIsPositive(false);
        anotherReview.setUserId(testUser.getId());
        anotherReview.setFilmId(testFilm.getId());
        reviewStorage.addReview(anotherReview);

        List<Review> reviews = reviewStorage.getAllReviews(10);
        assertEquals(2, reviews.size(), "Должны вернуться все отзывы");
    }
}