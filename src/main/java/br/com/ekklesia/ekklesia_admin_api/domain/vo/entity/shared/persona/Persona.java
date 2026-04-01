package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.base.BaseEntity;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.MaritalStatus;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "ek_persona")
public class Persona extends BaseEntity implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "persona_type")
    private PersonaType personaType;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "tax_id", length = 14)
    private String taxId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "phone_contact", length = 20)
    private String phone;

    @Column(name = "email_contact", length = 150)
    private String email;

    @Column(name = "address")
    private String address;

}
