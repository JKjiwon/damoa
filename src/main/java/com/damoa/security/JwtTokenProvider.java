package com.damoa.security;

import com.damoa.config.YamlPropertySourceFactory;
import com.damoa.web.dto.member.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * 토큰 생성 및 검증 로직
 */

@Component
@Slf4j
@PropertySource(value = "classpath:/jwt-info.yml", factory = YamlPropertySourceFactory.class)
public class JwtTokenProvider {

    public static final String INVALID_TOKEN_EXCEPTION = "INVALID_TOKEN_EXCEPTION";
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokenDto generateToken(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        long now = System.currentTimeMillis();
        Date expiryDate = new Date(now + jwtExpirationInMs);

        String accessToken = Jwts.builder()
                .setSubject(Long.toString(principalDetails.getMember().getId()))
                .setIssuedAt(new Date(now))
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new JwtTokenDto(accessToken, expiryDate);
    }

    public Long getMemberIdFromJWT(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken, HttpServletRequest request){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            request.setAttribute(INVALID_TOKEN_EXCEPTION, "Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            request.setAttribute(INVALID_TOKEN_EXCEPTION, "Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            request.setAttribute(INVALID_TOKEN_EXCEPTION, "Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            request.setAttribute(INVALID_TOKEN_EXCEPTION, "JWT claims string is empty.");
        }

        return false;
    }
}
