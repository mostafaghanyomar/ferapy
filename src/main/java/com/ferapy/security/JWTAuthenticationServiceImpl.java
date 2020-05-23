package com.ferapy.security;

import com.ferapy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.ferapy.security.JWTUtil.*;
import static com.ferapy.util.ReflectionUtils.getDeclaredFields;
import static com.ferapy.util.ReflectionUtils.getPropertyValue;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class JWTAuthenticationServiceImpl implements JWTAuthenticationService {

    private UserDetailsService userDetailsService;
    private UserRepository userRepository;
    private DaoAuthenticationProvider daoAuthenticationProvider;
    private JWTUtil jwtUtil;
    private PasswordEncoder encoder;

    @Override
    public Mono<Map<String, String>> login(JWTAuthCredentialsDTO jwtAuthCredentialsDTO) {
        //Authenticate the User by His Credentials
        final Authentication authentication = daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        jwtAuthCredentialsDTO.getUsername(),
                        jwtAuthCredentialsDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //Generate the Token and Return it.
        final String token = jwtUtil.generateToken((JWTUser) authentication.getPrincipal());
        return Mono.just(jwtUtil.wrapToken(token));
    }

    @Override
    public Mono<Map<String, String>> refreshAndGetBackAuthenticationToken(HttpServletRequest request) {
        String token = jwtUtil.getTokenFromRequest(request);
        String username = jwtUtil.getUsernameFromToken(token);
        final Optional<JWTUser> jwtUser = Optional.of((JWTUser) userDetailsService.loadUserByUsername(username));

        return jwtUser
                .filter(u -> jwtUtil.canTokenBeRefreshed(token, u.getLastModified()))
                .map(u -> jwtUtil.wrapToken(jwtUtil.refreshToken(token)))
                .map(Mono::just)
                .orElse(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!")));
    }

    @Override
    public Mono<Map<String, String>> signUp(JWTUserSignUpDTO jwtUserSignUpDTO) {
        if(!isValid(jwtUserSignUpDTO)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input"));
        }
        jwtUserSignUpDTO.setPassword(encoder.encode(jwtUserSignUpDTO.getPassword()));
        final JWTUser u = createJWTUser(userRepository.save(createUserDO(jwtUserSignUpDTO).audit()));
        daoAuthenticationProvider.getUserCache().putUserInCache(u);
        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());
        auth.setDetails(u);
        SecurityContextHolder.getContext().setAuthentication(auth);
        //Generate the Token and Return it.
        final String token = jwtUtil.generateToken(u);
        return Mono.just(jwtUtil.wrapToken(token));
    }

    private boolean isValid(JWTUserSignUpDTO jwtUserSignUpDTO) {
        return Arrays.stream(getDeclaredFields(JWTUserSignUpDTO.class))
                .filter(f -> !isStatic(f.getModifiers()))
                .map(Field::getName)
                .allMatch(f -> nonNull(getPropertyValue(jwtUserSignUpDTO, f)));
    }

}
