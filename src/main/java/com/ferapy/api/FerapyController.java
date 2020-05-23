package com.ferapy.api;

import com.ferapy.service.MatcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("${api.path}")
public class FerapyController {

    private MatcherService matcherService;

    @Operation(security = {@SecurityRequirement(name = "apikey")})
    @GetMapping("/listen")
    public Mono<ResponseEntity> listen(String sessionId, boolean video) {
        return matcherService.listen(sessionId, video);
    }

    @Operation(security = {@SecurityRequirement(name = "apikey")})
    @GetMapping("/talk")
    public Mono<ResponseEntity> talk(String sessionId, boolean video, boolean anonymous) {
        return matcherService.talk(sessionId, video, anonymous);
    }

}
