package com.ferapy.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

public interface JWTAuthenticationService {

    Mono<Map<String, String>> login(JWTAuthCredentialsDTO jwtAuthCredentialsDTO);

    Mono<Map<String, String>> refreshAndGetBackAuthenticationToken(HttpServletRequest request);

    Mono<Map<String, String>> signUp(JWTUserSignUpDTO jwtUserSignUpDTO);

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Accessors(chain = true)
    class JWTAuthCredentialsDTO implements Serializable {

        private static final long serialVersionUID = -3094979612860402078L;

        private String username;
        private String password;

    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    class JWTUserSignUpDTO {

        private String username;
        private String email;
        private String password;
        private String firstname;
        private String lastname;

    }

}
