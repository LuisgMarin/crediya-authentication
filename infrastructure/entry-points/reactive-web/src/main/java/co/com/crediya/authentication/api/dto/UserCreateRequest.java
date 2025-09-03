package co.com.crediya.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para crear un nuevo usuario")
public class UserCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;

    @NotNull(message = "La fecha de nacimiento es requerida")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15", required = true)
    private LocalDate birthDate;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
    private String address;

    @Schema(description = "Número de teléfono", example = "+573001234567")
    private String phone;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Email del usuario", example = "juan.perez@email.com", required = true)
    private String email;

    @NotNull(message = "El salario base es requerido")
    @DecimalMin(value = "0", message = "El salario base debe ser mayor o igual a 0")
    @DecimalMax(value = "15000000", message = "El salario base no puede exceder 15,000,000")
    @Digits(integer = 8, fraction = 2, message = "El salario debe tener máximo 8 dígitos enteros y 2 decimales")
    @Schema(description = "Salario base del usuario", example = "2500000.00", required = true)
    private BigDecimal baseSalary;

    @Min(value = 1, message = "El rol debe ser un valor válido")
    @Schema(description = "ID del rol del usuario", example = "1")
    private Integer role;
}