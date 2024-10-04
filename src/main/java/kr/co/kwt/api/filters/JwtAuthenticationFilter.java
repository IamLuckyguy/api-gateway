package kr.co.kwt.api.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final SecretKey key;

    public JwtAuthenticationFilter() {
        super(Config.class);
        String keyString = "rryy28uwpvqaqhdn4gasdlu1ozekkhbb";
        this.key = new SecretKeySpec(keyString.getBytes(), "HmacSHA256");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = getAuthHeader(exchange.getRequest());
            if (token == null || !validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // JWT가 유효하다면 SecurityContext에 설정
            Claims claims = getClaimsFromToken(token);
            exchange.getRequest().mutate().header("X-auth-user", claims.getSubject()).build();
            return chain.filter(exchange);
        };
    }

    // JWT 토큰을 검증하는 메서드
    private boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 클레임을 추출하는 메서드
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 헤더에서 JWT 토큰을 추출하는 메서드
    private String getAuthHeader(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return null;
        }
        String bearer = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!Objects.requireNonNull(bearer).startsWith("Bearer ")) {
            return null;
        }
        return bearer.substring(7);
    }

    public static class Config {
        // 필터 설정 관련 필드
    }
}
