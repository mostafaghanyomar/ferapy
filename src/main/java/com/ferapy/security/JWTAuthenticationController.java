package com.ferapy.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@RequestMapping("${api.auth}")
public class JWTAuthenticationController {

    private JWTAuthenticationService jwtAuthenticationService;

    @PostMapping("/login")
    public Mono<ResponseEntity> login(@RequestBody JWTAuthenticationService.JWTAuthCredentialsDTO jwtAuthCredentialsDTO) throws AuthenticationException {
        return jwtAuthenticationService.login(jwtAuthCredentialsDTO)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/sign-up")
    public Mono<ResponseEntity> signUp(@RequestBody JWTAuthenticationService.JWTUserSignUpDTO jwtUserSignUpDTO) {
        return jwtAuthenticationService.signUp(jwtUserSignUpDTO)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/refresh")
    public Mono<ResponseEntity> refreshAndGetBackAuthenticationToken(HttpServletRequest request) {
        return jwtAuthenticationService.refreshAndGetBackAuthenticationToken(request)
                .map(ResponseEntity::ok);
    }
}
