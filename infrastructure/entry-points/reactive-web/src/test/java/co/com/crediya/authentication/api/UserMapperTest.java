package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import co.com.crediya.authentication.api.dto.UserCreateRequest;
import co.com.crediya.authentication.api.dto.UserResponse;
import co.com.crediya.authentication.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private UserCreateRequest userCreateRequest;
    private User user;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void shouldMapUserCreateRequestToUser() {
        // When
        User result = userMapper.requestToUser(userCreateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // ID should be null for new users
        assertThat(result.getFirstName()).isEqualTo(userCreateRequest.getFirstName());
        assertThat(result.getLastName()).isEqualTo(userCreateRequest.getLastName());
        assertThat(result.getBirthDate()).isEqualTo(userCreateRequest.getBirthDate());
        assertThat(result.getAddress()).isEqualTo(userCreateRequest.getAddress());
        assertThat(result.getPhone()).isEqualTo(userCreateRequest.getPhone());
        assertThat(result.getEmail()).isEqualTo(userCreateRequest.getEmail());
        assertThat(result.getBaseSalary()).isEqualTo(userCreateRequest.getBaseSalary());
        assertThat(result.getRole()).isEqualTo(userCreateRequest.getRole());
    }

    @Test
    void shouldMapUserToUserResponse() {
        // When
        UserResponse result = userMapper.userToResponse(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(result.getLastName()).isEqualTo(user.getLastName());
        assertThat(result.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(result.getAddress()).isEqualTo(user.getAddress());
        assertThat(result.getPhone()).isEqualTo(user.getPhone());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getBaseSalary()).isEqualTo(user.getBaseSalary());
        assertThat(result.getRole()).isEqualTo(user.getRole());
    }

    @Test
    void shouldMapUserCreateRequestToUserWithNullOptionalFields() {
        // Given
        UserCreateRequest requestWithNulls = UserCreateRequest.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                // address and phone are null
                .build();

        // When
        User result = userMapper.requestToUser(requestWithNulls);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Pérez");
        assertThat(result.getAddress()).isNull();
        assertThat(result.getPhone()).isNull();
        assertThat(result.getEmail()).isEqualTo("juan.perez@email.com");
    }

    @Test
    void shouldMapUserToResponseWithNullOptionalFields() {
        // Given
        User userWithNulls = User.builder()
                .id("user-123")
                .firstName("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("juan.perez@email.com")
                .baseSalary(new BigDecimal("2500000.00"))
                .role(1)
                // address and phone are null
                .build();

        // When
        UserResponse result = userMapper.userToResponse(userWithNulls);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-123");
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Pérez");
        assertThat(result.getAddress()).isNull();
        assertThat(result.getPhone()).isNull();
        assertThat(result.getEmail()).isEqualTo("juan.perez@email.com");
    }
}