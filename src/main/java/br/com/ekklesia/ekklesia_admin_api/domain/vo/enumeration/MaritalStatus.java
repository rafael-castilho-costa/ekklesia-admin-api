package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

import lombok.Getter;

@Getter
public enum MaritalStatus implements Enumerable{

    CASADO("Casado(a)"),
    DIVORCIADO("Divorciado(a)"),
    SOLTEIRO("Solteiro(a)"),
    VIUVO("Viúvo(a)");

    private final String description;

    MaritalStatus(String desc) {
        this.description = desc;
    }

    @Override
    public EnumDto enumerable() {
        return new EnumDto(this.name(), this.getDescription());
    }
}
