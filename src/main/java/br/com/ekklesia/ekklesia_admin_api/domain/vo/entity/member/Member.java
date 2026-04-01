package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.base.BaseEntity;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.Ministry;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.StatusMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
@Entity
@Table(name = "ek_member")
public class Member extends BaseEntity implements Serializable {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private Persona persona;

    @Column(name = "membership_date")
    private LocalDate membershipDate;

    @Column(name = "baptism_date")
    private LocalDate baptismDate;

    @Column(name = "is_baptized")
    private Boolean baptized;

    @Enumerated(EnumType.STRING)
    @Column(name = "ministry")
    private Ministry ministry;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_member")
    private StatusMember statusMember;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
