package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

public enum Ministry implements Enumerable{

    YOUNG_PEOPLE("Jovens"),
    CHILDREN("Crianças"),
    PRE_TEENS("Pré-adolescentes"),
    WOMEN("Mulheres"),
    MEN("Homem");

    private final String description;

    Ministry(String description) {
        this.description = description;
    }
    @Override
    public EnumDto enumerable() {
        return new EnumDto(this.name(), this.description);
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
