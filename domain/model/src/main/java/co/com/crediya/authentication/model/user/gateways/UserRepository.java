package co.com.crediya.authentication.model.user.gateways;

import co.com.crediya.authentication.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> createUser(User user);
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Flux<User> findAll();

}
