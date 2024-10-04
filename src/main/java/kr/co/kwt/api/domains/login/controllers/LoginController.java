package kr.co.kwt.api.domains.login.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.kwt.api.domains.login.models.JwtResponseDto;
import kr.co.kwt.api.domains.login.models.LoginRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        // 사용자 인증 로직
        if (authenticateUser(loginRequestDto)) {
            var aKey = Keys.hmacShaKeyFor("rryy28uwpvqaqhdn4gasdlu1ozekkhbb".getBytes(StandardCharsets.UTF_8)); // TODO: KMS 등에서 받아온 값으로 변경 필요

            String token = Jwts.builder()
                    .subject(loginRequestDto.username())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 3600))  // 1시간
                    .signWith(aKey, Jwts.SIG.HS256)
                    .compact();
            return ResponseEntity.ok(new JwtResponseDto(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private boolean authenticateUser(LoginRequestDto loginRequestDto) {
        // 실제 사용자 인증 로직
        return "user".equals(loginRequestDto.username()) && "password".equals(loginRequestDto.password());
    }
}
