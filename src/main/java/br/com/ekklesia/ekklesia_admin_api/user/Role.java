package br.com.ekklesia.ekklesia_admin_api.user;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ek_role")
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}
