    package ru.yandex.practicum.filmorate.storage.likesStorage;

    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Repository;
    import ru.yandex.practicum.filmorate.storage.BaseStorage;

    import java.util.List;

    @Repository
    public class LikesDbStorage extends BaseStorage<Integer> {


        public LikesDbStorage(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate);
        }

        public void addLike(int userId, int filmId) {

            String query = """
                    INSERT INTO likes (user_id, film_id)
                    VALUES (?, ?);
                    """;

            insert(query, userId, filmId);
        }

        public void removeLike(int userId, int filmId) {

            String query = """
                    DELETE FROM likes
                    WHERE user_id = ?
                          AND film_id = ?;
                    """;

            delete(query, userId, filmId);
        }

        // Получаем все фильмы, которые лайкнул пользователь (возвращаем список film_id)
        public List<Integer> getLikesByUser(int userId) {
            String query = """
                SELECT film_id
                FROM likes
                WHERE user_id = ?;
                """;
            return jdbcTemplate.queryForList(query, Integer.class, userId);
        }

        // Получаем всех пользователей, которые лайкнули конкретный фильм
        public List<Integer> getUsersWhoLikedFilm(int filmId) {
            String query = """
                SELECT user_id
                FROM likes
                WHERE film_id = ?;
                """;
            return jdbcTemplate.queryForList(query, Integer.class, filmId);
        }
    }
