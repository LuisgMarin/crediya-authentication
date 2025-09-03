package co.com.crediya.authentication.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "createUser",
                            summary = "Registrar nuevo usuario",
                            description = "Registra un nuevo usuario en el sistema con sus datos personales",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.UserCreateRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.UserResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Error de validación",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/{id}",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getUserById",
                            summary = "Obtener usuario por ID",
                            description = "Obtiene la información de un usuario específico por su ID",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID del usuario"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.UserResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Obtener todos los usuarios",
                            description = "Obtiene la lista de todos los usuarios registrados en el sistema",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.UserResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = co.com.crediya.authentication.api.dto.ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler userHandler) {
        return route(POST("/api/v1/users")
                        .and(accept(MediaType.APPLICATION_JSON))
                        .and(contentType(MediaType.APPLICATION_JSON)),
                userHandler::listenCreateUser)
                .andRoute(GET("/api/v1/users/{id}"), userHandler::listenGetUser)
                .andRoute(GET("/api/v1/users"), userHandler::listenGetAllUsers);
    }
}