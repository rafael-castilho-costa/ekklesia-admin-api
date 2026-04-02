package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {

    private Integer personaId;

    public Integer getPersonaId() {
        return personaId;
    }
    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }
}
