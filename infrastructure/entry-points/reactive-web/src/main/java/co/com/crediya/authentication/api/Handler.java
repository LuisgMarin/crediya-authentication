package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.ErrorResponse;
import co.com.crediya.authentication.api.dto.UserCreateRequest;
import co.com.crediya.authentication.api.dto.UserResponse;
import co.com.crediya.authentication.api.mapper.UserMapper;
import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenCreateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserCreateRequest.class)
                .flatMap(this::validateRequest)
                .map(userMapper::requestToUser)
                .flatMap(userUseCase::createUser)
                .map(userMapper::userToResponse)
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> listenGetUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return userUseCase.findById(id)
                .map(userMapper::userToResponse)
                .flatMap(userResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.findAll()
                        .map(userMapper::userToResponse), UserResponse.class)
                .onErrorResume(this::handleError);
    }

    private Mono<UserCreateRequest> validateRequest(UserCreateRequest request) {
        return Mono.fromCallable(() -> {
            Errors errors = new BeanPropertyBindingResult(request, "userCreateRequest");
            validator.validate(request, errors);

            if (errors.hasErrors()) {
                List<String> errorMessages = errors.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());

                throw new BusinessValidationException("Errores de validación: " + String.join(", ", errorMessages));
            }

            return request;
        });
    }
    private Mono<ServerResponse> handleError(Throwable error) {
        log.error("Error en handler: ", error);

        ErrorResponse errorResponse;
        HttpStatus status;

        if (error instanceof BusinessValidationException) {
            errorResponse = ErrorResponse.builder()
                    .code("BUSINESS_VALIDATION_ERROR")
                    .message(error.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            status = HttpStatus.BAD_REQUEST;
        } else {
            errorResponse = ErrorResponse.builder()
                    .code("INTERNAL_ERROR")
                    .message("Error interno del servidor")
                    .timestamp(LocalDateTime.now())
                    .build();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}
