package br.com.ekklesia.ekklesia_admin_api.administration.user;

import br.com.ekklesia.ekklesia_admin_api.user.User;

import java.util.List;

public record AdminUserResponse(
        Integer id,
        String name,
        String email,
        boolean active,
        String scope,
        Integer personaId,
        String personaName,
        Long churchId,
        String churchName,
        List<String> roles
) {

    public static AdminUserResponse from(User user) {
        Long churchId = user.getChurch() != null
                ? user.getChurch().getId()
                : user.getPersona().getChurch() != null ? user.getPersona().getChurch().getId() : null;
        String churchName = user.getChurch() != null
                ? user.getChurch().getName()
                : user.getPersona().getChurch() != null ? user.getPersona().getChurch().getName() : null;

        return new AdminUserResponse(
                user.getId(),
                user.getPersona().getName(),
                user.getEmail(),
                user.isActive(),
                user.getScope().name(),
                user.getPersona().getId(),
                user.getPersona().getName(),
                churchId,
                churchName,
                user.getRoles().stream().map(role -> role.getName()).sorted().toList()
        );
    }
}
