package co.com.crediya.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error")
public class ErrorResponse {

    @Schema(description = "Código de error", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Mensaje principal del error", example = "Error de validación")
    private String message;

    @Schema(description = "Timestamp del error")
    private LocalDateTime timestamp;

}