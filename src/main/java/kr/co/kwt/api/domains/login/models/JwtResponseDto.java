package kr.co.kwt.api.domains.login.models;

import java.util.List;

public record JwtResponseDto(
        String token,
        String tokenType,
        String username,
        List<String> roles,
        int expiresIn
)
{
    public JwtResponseDto(String token, String username, List<String> roles, int expiresIn) {
        this(token, "Bearer", username, roles, expiresIn);
    }

    public JwtResponseDto(String token) {
        this(token, "Bearer", null, null, 0);
    }
}