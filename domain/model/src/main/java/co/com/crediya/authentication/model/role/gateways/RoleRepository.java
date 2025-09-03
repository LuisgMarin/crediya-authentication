package co.com.crediya.authentication.model.role.gateways;

import co.com.crediya.authentication.model.role.Role;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface RoleRepository {

    Mono<Role> getRolById(BigInteger id);

}
