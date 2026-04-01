package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

import static java.util.Optional.ofNullable;

public enum PersonaType implements Enumerable {

    NATURAL_PERSON("Pessoa Física"),
    LEGAL_PERSON("Pessoa Jurídica"),
    NOT_DEFINED("Não definida");

    private final String description;

    PersonaType(String desc) {
        this.description = desc;
    }

    public static PersonaType handleType(Integer lenght){
        return switch (ofNullable(lenght).orElse(0)){
            case 11 -> NATURAL_PERSON;
            case 14 -> LEGAL_PERSON;
            default -> NOT_DEFINED;
        };
    }

    @Override
    public EnumDto enumerable() {
        return new EnumDto(this.name(), this.getDescription());
    }

}
