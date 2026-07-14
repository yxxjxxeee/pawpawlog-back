package com.pawpawlog.global.security;

import com.pawpawlog.global.exception.CustomException;
import com.pawpawlog.global.exception.ErrorCode;
import com.pawpawlog.global.util.HttpResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (CustomException e) {
      log.warn("필터 체인 커스텀 예외 발생: {}", e.getErrorCode().name());
      writeError(response, e.getErrorCode());
    } catch (Exception e) {
      log.error("필터 체인 처리되지 않은 예외 발생", e);
      writeError(response, ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
    if (response.isCommitted()) {
      log.warn("응답이 이미 커밋되어 에러 응답을 쓸 수 없음: {}", errorCode.name());
      return;
    }
    HttpResponseUtil.writeErrorResponse(response, errorCode, objectMapper);
  }
}
