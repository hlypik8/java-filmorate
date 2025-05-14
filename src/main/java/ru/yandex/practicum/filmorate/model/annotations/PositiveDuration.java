package ru.yandex.practicum.filmorate.model.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.model.validators.PositiveDurationValidator;

import java.lang.annotation.*;

@Constraint(validatedBy = PositiveDurationValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveDuration {
    String message() default "Длительность должна быть положительной";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}