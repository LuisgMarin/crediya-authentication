package co.com.crediya.authentication.model;

import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.utils.UserValidate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

class UserValidateTest {

    @Test
    void shouldValidateValidUserSuccessfully() {
        // Given
        User validUser = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(validUser);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationWhenFirstNameIsNull() {
        // Given
        User userWithNullFirstName = User.builder()
                .firstName(null)
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNullFirstName);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El nombre es requerido"))
                .verify();
    }

    @Test
    void shouldFailValidationWhenFirstNameIsEmpty() {
        // Given
        User userWithEmptyFirstName = User.builder()
                .firstName("")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithEmptyFirstName);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El nombre es requerido"))
                .verify();
    }

    @Test
    void shouldFailValidationWhenFirstNameIsWhitespace() {
        // Given
        User userWithWhitespaceFirstName = User.builder()
                .firstName("   ")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithWhitespaceFirstName);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El nombre es requerido"))
                .verify();
    }

    @Test
    void shouldFailValidationWhenLastNameIsNull() {
        // Given
        User userWithNullLastName = User.builder()
                .firstName("Juan")
                .lastName(null)
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNullLastName);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El apellido es requerido"))
                .verify();
    }

    @Test
    void shouldFailValidationWhenEmailIsNull() {
        // Given
        User userWithNullEmail = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email(null)
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNullEmail);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El email es requerido"))
                .verify();
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "user123@test123.com",
            "a@b.co"
    })
    void shouldPassValidationForValidEmailFormats(String validEmail) {
        // Given
        User userWithValidEmail = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email(validEmail)
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithValidEmail);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationWhenBaseSalaryIsNull() {
        // Given
        User userWithNullSalary = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(null)
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNullSalary);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El salario base es requerido"))
                .verify();
    }

    @Test
    void shouldFailValidationWhenBirthDateIsNull() {
        // Given
        User userWithNullBirthDate = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(null)
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNullBirthDate);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("La fecha de nacimiento es requerida"))
                .verify();
    }

    @Test
    void shouldFailValidationForNegativeSalary() {
        // Given
        User userWithNegativeSalary = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("-1000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithNegativeSalary);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El salario base no puede ser negativo"))
                .verify();
    }

    @Test
    void shouldFailValidationForExcessiveSalary() {
        // Given
        User userWithHighSalary = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("15000001.00")) // Over the limit
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithHighSalary);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El salario base no puede exceder 15,000,000"))
                .verify();
    }

    @Test
    void shouldPassValidationForMaximumAllowedSalary() {
        // Given
        User userWithMaxSalary = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("15000000.00")) // Exactly at the limit
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithMaxSalary);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldPassValidationForZeroSalary() {
        // Given
        User userWithZeroSalary = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("0.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(userWithZeroSalary);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationForUnderageUser() {
        // Given
        User underageUser = User.builder()
                .firstName("Minor")
                .lastName("User")
                .birthDate(LocalDate.now().minusYears(17)) // 17 years old
                .email("minor@example.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        // When
        Mono<Void> result = UserValidate.validateUser(underageUser);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessValidationException &&
                                throwable.getMessage().equals("El usuario debe ser mayor de edad"))
                .verify();
    }
}