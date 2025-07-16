package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genreStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.*;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewDbStorage.class, ReviewRowMapper.class,
        UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class})
public class ReviewDbStorageTest {

    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    private User user;
    private Film film;

    @BeforeEach
    void setup() {
        // Создаем пользователя
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user = userDbStorage.newUser(user);

        // Создаем фильм
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(mpaDbStorage.getMpaById(1));
        film.setGenres(Set.of(genreDbStorage.getGenreById(1)));
        film = filmDbStorage.newFilm(film);
    }

    @Test
    void testCreateAndGetById() {
        Review review = new Review();
        review.setContent("Great movie");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());

        Review created = reviewDbStorage.addReview(review);
        assertNotNull(created);
        assertTrue(created.getReviewId() > 0);
        assertEquals("Great movie", created.getContent());
        assertTrue(created.getIsPositive());
        assertEquals(0, created.getUseful());

        Review fetched = reviewDbStorage.getReviewById(created.getReviewId());
        assertEquals(created.getReviewId(), fetched.getReviewId());
    }

    @Test
    void testUpdateReview() {
        Review review = new Review();
        review.setContent("Initial");
        review.setIsPositive(false);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        Review created = reviewDbStorage.addReview(review);

        created.setContent("Updated");
        created.setIsPositive(true);
        Review updated = reviewDbStorage.updateReview(created);

        assertAll("Обновленный отзыв",
                () -> assertEquals("Updated", updated.getContent()),
                () -> assertTrue(updated.getIsPositive())
        );
    }

    @Test
    void testDeleteReview() {
        Review review = new Review();
        review.setContent("To delete");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        Review created = reviewDbStorage.addReview(review);

        reviewDbStorage.deleteReview(created.getReviewId());
        Review deleted = reviewDbStorage.getReviewById(created.getReviewId());
        assertNull(deleted);
    }

    @Test
    void testGetByFilmIdAndAll() {
        for (int i = 0; i < 3; i++) {
            Review r = new Review();
            r.setContent("Review " + i);
            r.setIsPositive(true);
            r.setUserId(user.getId());
            r.setFilmId(film.getId());
            reviewDbStorage.addReview(r);
        }
        Collection<Review> byFilm = reviewDbStorage.getReviewsByFilmId(film.getId(), 5);
        assertEquals(3, byFilm.size());

        Collection<Review> all = reviewDbStorage.getAllReviews(5);
        assertTrue(all.size() >= 3);
    }

    @Test
    void testAddAndRemoveRating() {
        Review review = new Review();
        review.setContent("Rating test");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        Review created = reviewDbStorage.addReview(review);
        int id = created.getReviewId();

        // Лайк
        reviewDbStorage.addRating(id, user.getId(), true);
        Review liked = reviewDbStorage.getReviewById(id);
        assertEquals(1, liked.getUseful());

        // Дизлайк
        reviewDbStorage.addRating(id, user.getId(), false);
        Review disliked = reviewDbStorage.getReviewById(id);
        assertEquals(-1, disliked.getUseful());

        // Удаление
        reviewDbStorage.removeRating(id, user.getId());
        Review removed = reviewDbStorage.getReviewById(id);
        assertEquals(0, removed.getUseful());
    }
}
