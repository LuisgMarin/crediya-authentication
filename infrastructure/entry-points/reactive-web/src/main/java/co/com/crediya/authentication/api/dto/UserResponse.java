package co.com.crediya.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con información del usuario")
public class UserResponse {

    @Schema(description = "ID único del usuario", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate birthDate;

    @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
    private String address;

    @Schema(description = "Número de teléfono", example = "+573001234567")
    private String phone;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Salario base del usuario", example = "2500000.00")
    private BigDecimal baseSalary;

    @Schema(description = "ID del rol del usuario", example = "1")
    private Integer role;
}
