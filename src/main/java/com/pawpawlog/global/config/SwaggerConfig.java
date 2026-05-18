package com.pawpawlog.global.config;

import com.pawpawlog.auth.dto.request.LoginRequest;
import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.global.response.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    Components components = new Components()
        .addSecuritySchemes("BearerAuth", new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization"));

    ModelConverters.getInstance().read(LoginRequest.class)
        .forEach(components::addSchemas);
    ModelConverters.getInstance().read(TokenResponse.class)
        .forEach(components::addSchemas);
    ModelConverters.getInstance().read(ErrorResponse.class)
        .forEach(components::addSchemas);

    return new OpenAPI()
        .info(new Info()
            .title("PawPawLog API")
            .description("포포로그 API 명세서")
            .version("v1.0.0"))
        .components(components)
        .path("/auth/login", loginPathItem());
  }

  private PathItem loginPathItem() {
    return new PathItem().post(new Operation()
        .tags(List.of("Auth"))
        .summary("로그인")
        .description("username / password로 로그인하여 JWT 토큰을 발급받습니다.")
        .requestBody(new RequestBody()
            .required(true)
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginRequest")))))
        .responses(new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                .description("로그인 성공")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/TokenResponse")))))
            .addApiResponse("401", new ApiResponse()
                .description("아이디 또는 비밀번호 불일치")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))));
  }
}
