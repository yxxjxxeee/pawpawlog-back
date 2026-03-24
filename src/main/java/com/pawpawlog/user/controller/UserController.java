package com.pawpawlog.user.controller;

import com.pawpawlog.global.response.ApiResponse;
import com.pawpawlog.user.dto.request.SignUpRequest;
import com.pawpawlog.user.dto.response.UserResponse;
import com.pawpawlog.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/verify-username")
  public ResponseEntity<ApiResponse<Void>> verifyUsername(
      @RequestParam String username) {

    userService.verifyUsername(username);
    return ResponseEntity.ok(ApiResponse.ok("사용 가능한 아이디입니다."));
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<UserResponse>> signUp(
      @RequestBody @Valid SignUpRequest request) {

    UserResponse response = userService.signUp(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.ok("회원가입이 완료되었습니다.", response));
  }
}