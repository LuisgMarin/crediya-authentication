package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.UserCreateRequest;
import co.com.crediya.authentication.api.dto.UserResponse;
import co.com.crediya.authentication.api.mapper.UserMapper;
import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private Handler handler;

    private UserCreateRequest userCreateRequest;
    private User user;
    private UserResponse userResponse;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        userCreateRequest = UserCreateRequest.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Calle 123 #45-67")
                .phone("+573001234567")
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                .build();

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

        userResponse = UserResponse.builder()
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
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(org.springframework.http.HttpMethod.GET)
                .uri(java.net.URI.create("/api/v1/users/user-123"))
                .pathVariable("id", "user-123")
                .build();

        when(userUseCase.findById("user-123")).thenReturn(Mono.just(user));
        when(userMapper.userToResponse(any(User.class))).thenReturn(userResponse);

        // When
        Mono<ServerResponse> response = handler.listenGetUser(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);
                    return true;
                })
                .verifyComplete();

        verify(userUseCase).findById("user-123");
        verify(userMapper).userToResponse(any(User.class));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        // Given
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(org.springframework.http.HttpMethod.GET)
                .uri(java.net.URI.create("/api/v1/users/non-existing-user"))
                .pathVariable("id", "non-existing-user")
                .build();

        when(userUseCase.findById("non-existing-user")).thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> response = handler.listenGetUser(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    return true;
                })
                .verifyComplete();

        verify(userUseCase).findById("non-existing-user");
        verifyNoInteractions(userMapper);
    }

}