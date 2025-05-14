package ru.yandex.practicum.filmorate.model.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.model.validators.ReleaseDateValidator;

import java.lang.annotation.*;

@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateConstraint {
    String message() default "Неверная дата выхода";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
