package co.com.crediya.authentication.usecase.user;

import co.com.crediya.authentication.model.exceptions.BusinessValidationException;
import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.model.utils.UserValidate;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.logging.Logger;
import java.util.logging.Level;

@RequiredArgsConstructor
public class UserUseCase {

    private static final Logger log = Logger.getLogger(UserUseCase.class.getName());
    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return UserValidate.validateUser(user)
                .then(validateEmailNotExists(user.getEmail()))
                .then(userRepository.createUser(user))
                .doOnSuccess(savedUser ->
                        log.info("Usuario registrado exitosamente " + savedUser))
                .doOnError(error -> {
                    if (error instanceof BusinessValidationException) {
                        log.warning("Error de validación: " + error.getMessage());
                    } else {
                        log.log(Level.SEVERE, "Error inesperado al registrar usuario", error);
                    }
                });
    }

    public Mono<User> findById(String id) {
        log.fine("Buscando usuario por ID: " + id);

        return userRepository.findById(id)
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.fine("Usuario encontrado: " + id);
                    }
                });
    }

    public Flux<User> findAll() {
        log.fine("Obteniendo lista de usuarios");

        return userRepository.findAll();
    }

    private Mono<Void> validateEmailNotExists(String email) {
        return userRepository.findByEmail(email)
                .flatMap(existingUser -> Mono.error(
                        new BusinessValidationException("El usuario ya existe: " + email)))
                .then();
    }
}