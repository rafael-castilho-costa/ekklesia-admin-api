package br.com.ekklesia.ekklesia_admin_api.church;

import java.time.LocalDateTime;

public record ChurchResponse(
        Long id,
        String name,
        String cnpj,
        String city,
        String state,
        LocalDateTime createdAt
) {

    public static ChurchResponse from(Church church) {
        return new ChurchResponse(
                church.getId(),
                church.getName(),
                church.getCnpj(),
                church.getCity(),
                church.getState(),
                church.getCreatedAt()
        );
    }
}
