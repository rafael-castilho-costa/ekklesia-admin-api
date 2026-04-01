package br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumDto {
    private String value;
    private String description;

    public EnumDto(){

    }

    public EnumDto(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
