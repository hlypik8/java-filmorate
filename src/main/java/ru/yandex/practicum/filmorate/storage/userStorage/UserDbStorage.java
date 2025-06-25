package ru.yandex.practicum.filmorate.storage.userStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;

@Component
@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private final UserRowMapper userRowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        super(jdbcTemplate);
        this.userRowMapper = userRowMapper;
    }

    /* public boolean isFriends(int userId, int friendId) {

         String query = """
                 SELECT EXISTS(
                     SELECT 1
                     FROM friends
                     WHERE  ((user_id = ? AND friend_id = ?)
                            OR (user_id = ? AND friend_id = ?))
                       AND accepted = true
                 );
                 """;

         Boolean result = jdbcTemplate.queryForObject(
                 query, Boolean.class,
                 userId, friendId,
                 friendId, userId);

         return Boolean.TRUE.equals(result);
     }
 */
    @Override
    public Collection<User> getUsersList() {

        String query = """
                SELECT * 
                FROM users 
                ORDER BY id ASC;
                """;

        return findMany(query, userRowMapper);
    }

    @Override
    public User newUser(User user) {

        String query = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (?,?,?,?);
                """;

        int id = insert(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()));

        user.setId(id);

        return user;
    }

    @Override
    public void removeUser(int userId) {

        String query = """
                DELETE FROM users
                WHERE id = ?;
                """;

        if (!delete(query, userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User updateUser(User user) throws NotFoundException {

        String query = """
                UPDATE users SET email = ?, login = ?, name = ?, birthday = ?
                WHERE id = ?;
                """;

        update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());

        return user;
    }

    @Override
    public User getUserById(int userId) throws NotFoundException {

        String query = """
                SELECT *
                FROM users
                WHERE id = ?;
                """;

        User user = findOne(query, userRowMapper, userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }
}
