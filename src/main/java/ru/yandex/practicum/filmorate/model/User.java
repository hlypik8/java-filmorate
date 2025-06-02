package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {

    private final Set<Integer> friends = new HashSet<>();

    public void addNewFriend(int id) {
        friends.add(id);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    private int id;

    @Email(message = "Введен неверный адрес электронной почты!")
    private String email;

    @NotBlank(message = "Логин не может быть пустым!")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов!")
    private String login;

    private String name;

    @Past(message = "Дата рождения должна быть в прошлом!")
    private LocalDate birthday;

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = this.login;
        } else {
            this.name = name;
        }
    }
}
