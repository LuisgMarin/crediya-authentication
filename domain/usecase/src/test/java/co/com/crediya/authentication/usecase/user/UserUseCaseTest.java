package co.com.crediya.authentication.usecase.user;

import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Logger logger;

    private UserUseCase userUseCase;

    private User validUser;
    private User invalidUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository);

        validUser = User.builder()
                .id("user-123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Calle 123 #45-67")
                .phone("+573001234567")
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        invalidUser = User.builder()
                .firstName("") // Invalid: empty name
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        existingUser = User.builder()
                .id("existing-user")
                .firstName("Existing")
                .lastName("User")
                .birthDate(LocalDate.of(1985, 3, 20))
                .email("existing@email.com")
                .baseSalary(new BigDecimal("3000000.00"))
                .role(1)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User userWithoutId = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Calle 123 #45-67")
                .phone("+573001234567")
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Mono.empty());
        when(userRepository.createUser(any(User.class))).thenReturn(Mono.just(validUser));

        // When
        Mono<User> result = userUseCase.createUser(userWithoutId);

        // Then
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findByEmail("juan.perez@email.com");
        verify(userRepository).createUser(any(User.class));
    }


    @Test
    void shouldHandleRepositoryErrorDuringCreation() {
        // Given
        User userWithoutId = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Mono.empty());
        when(userRepository.createUser(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<User> result = userUseCase.createUser(userWithoutId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByEmail("juan.perez@email.com");
        verify(userRepository).createUser(any(User.class));
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        // Given
        String userId = "user-123";
        when(userRepository.findById(userId)).thenReturn(Mono.just(validUser));

        // When
        Mono<User> result = userUseCase.findById(userId);

        // Then
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        String nonExistingUserId = "non-existing-user";
        when(userRepository.findById(nonExistingUserId)).thenReturn(Mono.empty());

        // When
        Mono<User> result = userUseCase.findById(nonExistingUserId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findById(nonExistingUserId);
    }

    @Test
    void shouldHandleRepositoryErrorInFindById() {
        // Given
        String userId = "user-123";
        when(userRepository.findById(userId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<User> result = userUseCase.findById(userId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldFindAllUsersSuccessfully() {
        // Given
        User anotherUser = User.builder()
                .id("user-456")
                .firstName("María")
                .lastName("González")
                .birthDate(LocalDate.of(1985, 3, 20))
                .email("maria.gonzalez@email.com")
                .baseSalary(new BigDecimal("3000000.00"))
                .role(2)
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(validUser, anotherUser));

        // When
        Flux<User> result = userUseCase.findAll();

        // Then
        StepVerifier.create(result)
                .expectNext(validUser)
                .expectNext(anotherUser)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsersFound() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<User> result = userUseCase.findAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldHandleRepositoryErrorInFindAll() {
        // Given
        when(userRepository.findAll())
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        // When
        Flux<User> result = userUseCase.findAll();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findAll();
    }

    @Test
    void shouldCreateUserWithMinimumValidData() {
        // Given - User with only required fields
        User minimalUser = User.builder()
                .firstName("Ana")
                .lastName("López")
                .birthDate(LocalDate.of(1995, 8, 10))
                .email("ana.lopez@email.com")
                .baseSalary(new BigDecimal("1800000.00"))
                .role(1)
                // Optional fields (address, phone) are null
                .build();

        User savedUser = User.builder()
                .id("user-456")
                .firstName("Ana")
                .lastName("López")
                .birthDate(LocalDate.of(1995, 8, 10))
                .email("ana.lopez@email.com")
                .baseSalary(new BigDecimal("1800000.00"))
                .role(1)
                .build();

        when(userRepository.findByEmail("ana.lopez@email.com")).thenReturn(Mono.empty());
        when(userRepository.createUser(any(User.class))).thenReturn(Mono.just(savedUser));

        // When
        Mono<User> result = userUseCase.createUser(minimalUser);

        // Then
        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository).findByEmail("ana.lopez@email.com");
        verify(userRepository).createUser(any(User.class));
    }
}