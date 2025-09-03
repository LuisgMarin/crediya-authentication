package co.com.crediya.authentication.r2dbc;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private UserReactiveRepositoryAdapter repositoryAdapter;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new UserReactiveRepositoryAdapter(repository, mapper, transactionalOperator);

        user = User.builder()
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

        userEntity = UserEntity.builder()
                .id("user-123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Calle 123 #45-67")
                .phone("+573001234567")
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1L)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> {
            Mono<?> mono = invocation.getArgument(0);
            return mono; // Simulate transactional behavior
        });

        // When
        Mono<User> result = repositoryAdapter.createUser(user);

        // Then
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(mapper).map(user, UserEntity.class);
        verify(repository).save(userEntity);
        verify(mapper).map(userEntity, User.class);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(repository.findById("user-123")).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // When
        Mono<User> result = repositoryAdapter.findById("user-123");

        // Then
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(repository).findById("user-123");
        verify(mapper).map(userEntity, User.class);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        when(repository.findById("non-existing-user")).thenReturn(Mono.empty());

        // When
        Mono<User> result = repositoryAdapter.findById("non-existing-user");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findById("non-existing-user");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        // Given
        when(repository.findByEmail("juan.perez@email.com")).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        // When
        Mono<User> result = repositoryAdapter.findByEmail("juan.perez@email.com");

        // Then
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(repository).findByEmail("juan.perez@email.com");
        verify(mapper).map(userEntity, User.class);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // Given
        when(repository.findByEmail("nonexistent@email.com")).thenReturn(Mono.empty());

        // When
        Mono<User> result = repositoryAdapter.findByEmail("nonexistent@email.com");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findByEmail("nonexistent@email.com");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldFindAllUsersSuccessfully() {
        // Given
        User anotherUser = User.builder()
                .id("user-456")
                .firstName("María")
                .lastName("González")
                .email("maria.gonzalez@email.com")
                .birthDate(LocalDate.of(1985, 3, 20))
                .baseSalary(new BigDecimal("3000000.00"))
                .role(2)
                .build();

        UserEntity anotherUserEntity = UserEntity.builder()
                .id("user-456")
                .firstName("María")
                .lastName("González")
                .email("maria.gonzalez@email.com")
                .birthDate(LocalDate.of(1985, 3, 20))
                .baseSalary(new BigDecimal("3000000.00"))
                .role(2L)
                .build();

        when(repository.findAll()).thenReturn(Flux.just(userEntity, anotherUserEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(anotherUserEntity, User.class)).thenReturn(anotherUser);

        // When
        Flux<User> result = repositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectNext(user)
                .expectNext(anotherUser)
                .verifyComplete();

        verify(repository).findAll();
        verify(mapper, times(2)).map(any(UserEntity.class), eq(User.class));
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsersFound() {
        // Given
        when(repository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<User> result = repositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findAll();
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldCheckIfEmailExistsSuccessfully() {
        // Given
        when(repository.existsByEmail("juan.perez@email.com")).thenReturn(Mono.just(true));

        // When
        Mono<Boolean> result = repositoryAdapter.existsByEmail("juan.perez@email.com");

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByEmail("juan.perez@email.com");
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        when(repository.existsByEmail("nonexistent@email.com")).thenReturn(Mono.just(false));

        // When
        Mono<Boolean> result = repositoryAdapter.existsByEmail("nonexistent@email.com");

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsByEmail("nonexistent@email.com");
    }

    @Test
    void shouldHandleErrorInCreateUser() {
        // Given
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.error(new RuntimeException("Database error")));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> {
            Mono<?> mono = invocation.getArgument(0);
            return mono; // Pass through the error
        });

        // When
        Mono<User> result = repositoryAdapter.createUser(user);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).map(user, UserEntity.class);
        verify(repository).save(userEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldHandleErrorInFindById() {
        // Given
        when(repository.findById("user-123")).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<User> result = repositoryAdapter.findById("user-123");

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findById("user-123");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldHandleErrorInFindByEmail() {
        // Given
        when(repository.findByEmail("juan.perez@email.com"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<User> result = repositoryAdapter.findByEmail("juan.perez@email.com");

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail("juan.perez@email.com");
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldHandleErrorInFindAll() {
        // Given
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When
        Flux<User> result = repositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findAll();
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldHandleErrorInExistsByEmail() {
        // Given
        when(repository.existsByEmail("juan.perez@email.com"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<Boolean> result = repositoryAdapter.existsByEmail("juan.perez@email.com");

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).existsByEmail("juan.perez@email.com");
    }

}