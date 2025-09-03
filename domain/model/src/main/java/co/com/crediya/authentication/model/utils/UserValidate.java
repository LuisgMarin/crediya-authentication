package co.com.crediya.authentication.model.utils;

import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;


public class UserValidate {


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static Mono<Void> validateUser(User user) {
        return Mono.fromRunnable(() -> {
            validateRequiredFields(user);
            validateEmailFormat(user.getEmail());
            validateSalaryRange(user.getBaseSalary());
            validateAge(user.getBirthDate());
        });
    }

    private static void validateRequiredFields(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new BusinessValidationException("El nombre es requerido");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new BusinessValidationException("El apellido es requerido");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BusinessValidationException("El email es requerido");
        }
        if (user.getBaseSalary() == null) {
            throw new BusinessValidationException("El salario base es requerido");
        }
        if (user.getBirthDate() == null) {
            throw new BusinessValidationException("La fecha de nacimiento es requerida");
        }
    }

    private static void validateEmailFormat(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessValidationException("El formato del email no es válido");
        }
    }


    private static void validateSalaryRange(BigDecimal baseSalary) {
        if (baseSalary != null) {
            if (baseSalary.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessValidationException("El salario base no puede ser negativo");
            }
            if (baseSalary.compareTo(new BigDecimal("15000000")) > 0) {
                throw new BusinessValidationException("El salario base no puede exceder 15,000,000");
            }
        }
    }

    private static void validateAge(LocalDate birthDate) {
        if (birthDate != null) {
            LocalDate today = LocalDate.now();
            Period period = Period.between(birthDate, today);
            if (period.getYears() < 18) {
                throw new BusinessValidationException("El usuario debe ser mayor de edad");
            }
        }
    }
}
