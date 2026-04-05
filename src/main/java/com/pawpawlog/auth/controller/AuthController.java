package com.pawpawlog.auth.controller;

import com.pawpawlog.auth.dto.request.ReissueRequest;
import com.pawpawlog.auth.dto.response.TokenResponse;
import com.pawpawlog.auth.service.AuthService;
import com.pawpawlog.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/reissue")
  public ResponseEntity<ApiResponse<TokenResponse>> reissue(
      @RequestBody @Valid ReissueRequest request) {
    return ResponseEntity.ok(ApiResponse.success(authService.reissue(request)));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader("Authorization") String bearerToken) {
    authService.logout(bearerToken);
    return ResponseEntity.ok(ApiResponse.success());
  }
}