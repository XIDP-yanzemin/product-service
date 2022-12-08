package com.practice.productservice.util;

import com.auth0.jwt.JWT;
import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.exception.UserAuthenticationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class JwtService {
    public static final Instant INSTANT_NOW_AT_CURRENT_TIME_ZONE = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));


    public Long decodeIdFromJwt(String token) {
        return JWT.decode(token).getClaim("id").asLong();
    }

    public Boolean isTokenExpValid(String token) throws UserAuthenticationException {
        if (JWT.decode(token).getClaim("exp").asInstant().isBefore(INSTANT_NOW_AT_CURRENT_TIME_ZONE)) {
            throw new UserAuthenticationException(ErrorCode.TOKEN_EXPIRED_EXCEPTION);
        }
        return true;
    }
}
