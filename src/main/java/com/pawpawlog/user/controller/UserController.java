package com.pawpawlog.user.controller;

import com.pawpawlog.user.dto.request.SignUpRequest;
import com.pawpawlog.user.dto.response.UserResponse;
import com.pawpawlog.user.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @GetMapping("/usernames/{username}")
  public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
    return ResponseEntity.ok(userService.existsByUsername(username));
  }

  @PostMapping
  public ResponseEntity<UserResponse> signUp(@RequestBody @Valid SignUpRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(request));
  }
}
