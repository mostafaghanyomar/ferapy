package com.ferapy.security;

import com.ferapy.document.UserDO;
import com.ferapy.document.constant.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class JWTUtil {

    private static final String EXPIRATION_DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.bearer} ")
    private String tokenBearer;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return doGenerateToken(userDetails.getUsername());
    }

    public Boolean canTokenBeRefreshed(String token, Date userLastModification) {
        final Date created = getClaimFromToken(token, Claims::getIssuedAt);
        return !isCreatedBeforeLastModificationToUser(created, userLastModification)
                && !isTokenExpired(token);
    }

    public String refreshToken(String token) {
        final Date createdDate = now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        final String requestHeader = request.getHeader(this.tokenHeader);
        String authToken = null;
        if (nonNull(requestHeader) && requestHeader.startsWith(tokenBearer)) {
            authToken = requestHeader.substring(tokenBearer.length());
        }
        return authToken;
    }

    public Boolean isValidToken(String token, UserDetails userDetails) {
        if (isNull(token) || isNull(userDetails)) {
            return Boolean.FALSE;
        }
        JWTUser user = (JWTUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getClaimFromToken(token, Claims::getIssuedAt);
        return username.equals(user.getUsername())
                && !isTokenExpired(token)
                && !isCreatedBeforeLastModificationToUser(created, user.getLastModified());
    }

    public Map<String, String> wrapToken(String token) {
        Map<String, String> tokenWrapper = new HashMap<>();
        tokenWrapper.put("token", token);
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        tokenWrapper.put("expirationDate", new SimpleDateFormat(EXPIRATION_DATE_FORMAT).format(expiration));
        return tokenWrapper;
    }

    public static JWTUser getCurrentJWTUser() {
        return (JWTUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static <T, R> R copyBeanProperties(T source, R target, String... ignoredProperties) {
        BeanUtils.copyProperties(source, target, ignoredProperties);
        return target;
    }

    public static JWTUser createJWTUser(UserDO user) {
        return copyBeanProperties(user, new JWTUser(), "authorities");
    }

    public static UserDO createUserDO(JWTAuthenticationService.JWTUserSignUpDTO user) {
        return copyBeanProperties(user, new UserDO())
                .setAuthorities(Arrays.asList(Role.values()));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(now());
    }

    private Boolean isCreatedBeforeLastModificationToUser(Date created, Date userLastModification) {
        return (nonNull(userLastModification) && created.before(userLastModification));
    }

    private String doGenerateToken(String subject) {
        final Date createdDate = now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiresAfter());
    }

    private Date now() {
        return new Date();
    }

    private Long expiresAfter() {
        return Arrays.stream(expiration.split("[*]"))
                .mapToLong(Long::parseLong).reduce(1, Math::multiplyExact);
    }

}
