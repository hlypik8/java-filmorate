package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.eventEnums.Operation;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final EventService eventService;

    public Review addReview(Review review) {
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());
        reviewStorage.addReview(review);
        eventService.createReviewEvent(review.getUserId(), Operation.ADD, review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        validateReview(review.getReviewId());
        Review updatedReview = reviewStorage.updateReview(review);
        eventService.createReviewEvent(updatedReview.getUserId(), Operation.UPDATE, updatedReview.getReviewId());
        return updatedReview;
    }

    public void deleteReview(int id) {
        eventService.createReviewEvent(reviewStorage.getReviewById(id).getUserId(), Operation.REMOVE, id);
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(int id) {
        Review review = reviewStorage.getReviewById(id);
        if (review == null) {
            throw new ReviewNotFoundException("Отзыв не найден");
        }
        return review;
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId != null) {
            return reviewStorage.getReviewsByFilmId(filmId, count).stream().toList();
        }
        return reviewStorage.getAllReviews(count).stream().toList();
    }

    public void addLike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.addRating(reviewId, userId, true);
    }

    public void addDislike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.addRating(reviewId, userId, false);
    }

    public void removeLike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.removeRating(reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.removeRating(reviewId, userId);
    }

    private void validateUser(int userId) {
        if (!userStorage.exists(userId)) {
            log.warn("Пользователь c id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateFilm(int filmId) {
        if (!filmStorage.exists(filmId)) {
            log.warn("Фильм c id {} не найден", filmId);
            throw new NotFoundException("Фильм не найден");
        }
    }

    private void validateReview(int reviewId) {
        if (!reviewStorage.exists(reviewId)) {
            log.warn("Отзыв c id {} не найден", reviewId);
            throw new ReviewNotFoundException("Отзыв не найден");
        }
    }
}