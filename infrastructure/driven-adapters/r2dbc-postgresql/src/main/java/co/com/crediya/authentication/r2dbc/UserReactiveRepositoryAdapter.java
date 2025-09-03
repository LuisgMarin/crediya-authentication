package co.com.crediya.authentication.r2dbc;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.model.user.gateways.UserRepository;
import co.com.crediya.authentication.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.authentication.r2dbc.entity.UserEntity;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

@Repository
public class UserReactiveRepositoryAdapter extends
        ReactiveAdapterOperations<User, UserEntity, String, UserReactiveRepository>
        implements UserRepository {

    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper,
                                 TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> createUser(User user) {
        return super.save(user).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findById(String id) {
        return super.findById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> mapper.map(entity, User.class));
    }

    @Override
    public Flux<User> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}