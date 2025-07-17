package ru.yandex.practicum.filmorate.storage.reviewStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.Collection;

@Repository
public class ReviewDbStorage extends BaseStorage<Review> {
    private final ReviewRowMapper reviewRowMapper;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate,
                           ReviewRowMapper reviewRowMapper) {
        super(jdbcTemplate);
        this.reviewRowMapper = reviewRowMapper;
    }

    public Review addReview(Review review) {

        String query = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?,?,?,?,?);
            """;

        int id = insert(query,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0);

        review.setReviewId(id);

        return review;
    }

    public Review updateReview(Review review) {

        String query = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE review_id = ?;
            """;

        update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getReviewById(review.getReviewId());
    }

    public void deleteReview(int id) {

        String query = """
            DELETE FROM reviews
            WHERE review_id = ?;
            """;

        delete(query, id);
    }


    public Review getReviewById(int id) {

        String query = """
                SELECT *
                FROM reviews
                WHERE review_id = ?;
                """;

        return findOne(query, reviewRowMapper, id);
    }

    public Collection<Review> getReviewsByFilmId(Integer filmId, int count) {

        String query = """
                SELECT *
                FROM reviews
                WHERE film_id = ?
                ORDER BY useful DESC
                LIMIT ?;
                """;

        return findMany(query, reviewRowMapper, filmId, count);
    }

    public Collection<Review> getAllReviews(int count) {

        String query = """
                SELECT *
                FROM reviews
                ORDER BY useful DESC
                LIMIT ?;
                """;

        return findMany(query, reviewRowMapper, count);
    }

    public void addRating(int reviewId, int userId, boolean isLike) {

        String query = """
                MERGE INTO review_ratings (review_id, user_id, is_positive)
                VALUES (?, ?, ?);
                """;

        update(query, reviewId, userId, isLike);
        updateUseful(reviewId);
    }

    public void removeRating(int reviewId, int userId) {

        String query = """
                DELETE FROM review_ratings
                WHERE review_id = ? AND user_id = ?
                """;

        update(query, reviewId, userId);
        updateUseful(reviewId);
    }

    private void updateUseful(int reviewId) {

        String query = """
                UPDATE reviews SET useful = (SELECT COALESCE(SUM(CASE WHEN is_positive THEN 1 ELSE -1 END), 0)
                FROM review_ratings WHERE review_id = ?)
                WHERE review_id = ?;
                """;
        update(query, reviewId, reviewId);
    }
}