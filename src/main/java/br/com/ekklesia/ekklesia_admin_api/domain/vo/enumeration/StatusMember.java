package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

public enum StatusMember implements  Enumerable {

    ACTIVE("Ativo"),
    VISITOR("Visitante"),
    AWAY("Afastado"),
    INACTIVE("Inativo");

    private final String description;

    StatusMember(String description) {
        this.description = description;
    }

    @Override
    public EnumDto enumerable() {
        return new EnumDto(this.name(), this.description);
    }

    @Override
    public  String getDescription() {
        return this.description;
    }
}
