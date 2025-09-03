package co.com.crediya.authentication.api.mapper;

import co.com.crediya.authentication.api.dto.UserCreateRequest;
import co.com.crediya.authentication.api.dto.UserResponse;
import co.com.crediya.authentication.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User requestToUser(UserCreateRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .baseSalary(request.getBaseSalary())
                .role(request.getRole())
                .build();
    }

    public UserResponse     userToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phone(user.getPhone())
                .email(user.getEmail())
                .baseSalary(user.getBaseSalary())
                .role(user.getRole())
                .build();
    }
}