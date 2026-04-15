package br.com.ekklesia.ekklesia_admin_api.auth;

import java.util.List;

public record AuthMeResponse(
        Integer userId,
        String email,
        String name,
        Integer personaId,
        Long churchId,
        String churchName,
        List<String> roles
) {
}
