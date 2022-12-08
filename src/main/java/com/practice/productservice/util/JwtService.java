package com.practice.productservice.util;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

@Component
public class JwtService {


    public Long decodeIdFromJwt(String token) {
        return JWT.decode(token).getClaim("id").asLong();
    }
}
