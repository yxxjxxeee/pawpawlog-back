package com.pawpawlog.global.health;

import com.pawpawlog.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public ResponseEntity<ApiResponse<String>> health() {
    return ResponseEntity.ok(ApiResponse.success("ok"));
  }
}
