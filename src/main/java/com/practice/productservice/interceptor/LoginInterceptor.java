package com.practice.productservice.interceptor;

import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.exception.UserAuthenticationException;
import com.practice.productservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String url = request.getRequestURI();

        if (url.contains("/login") || url.contains("/signup") || url.contains("/verification") || url.contains("/logout")) {
            return true;
        }

        String token = request.getHeader("token");
        if (Objects.nonNull(token) && !token.isEmpty()) {
            return jwtService.isTokenExpValid(token);
        }
        throw new UserAuthenticationException(ErrorCode.NOT_LOG_IN_ERROR);
    }
}
