package br.com.ekklesia.ekklesia_admin_api.auth;

public record AuthenticationRequest(
        String email,
        String password
) { }
