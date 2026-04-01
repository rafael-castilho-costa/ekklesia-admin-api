package br.com.ekklesia.ekklesia_admin_api.user;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.base.BaseEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity

@Table(name = "ek_role")
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ek_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
