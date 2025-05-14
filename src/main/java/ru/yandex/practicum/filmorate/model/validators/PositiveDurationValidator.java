package ru.yandex.practicum.filmorate.model.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.annotations.PositiveDuration;

import java.time.Duration;

public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        return duration != null && !duration.isNegative() && !duration.isZero();
    }
}