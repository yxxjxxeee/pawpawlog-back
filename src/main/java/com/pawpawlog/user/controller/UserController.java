package com.pawpawlog.user.controller;

import com.pawpawlog.global.response.ErrorResponse;
import com.pawpawlog.user.dto.request.SignUpRequest;
import com.pawpawlog.user.dto.response.UserResponse;
import com.pawpawlog.user.dto.response.UsernameAvailabilityResponse;
import com.pawpawlog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Operation(summary = "아이디 사용 가능 여부 확인", description = "해당 아이디가 이미 사용 중인지 확인합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "확인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsernameAvailabilityResponse.class)))})
  @GetMapping("/usernames/{username}")
  public ResponseEntity<UsernameAvailabilityResponse> checkUsernameAvailability(
      @PathVariable String username) {
    return ResponseEntity.ok(userService.checkUsernameAvailability(username));
  }

  @Operation(summary = "회원가입", description = "username / password / nickname으로 신규 계정을 생성합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
      @ApiResponse(responseCode = "400", description = "입력값 유효성 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping
  public ResponseEntity<UserResponse> signUp(@RequestBody @Valid SignUpRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(request));
  }
}