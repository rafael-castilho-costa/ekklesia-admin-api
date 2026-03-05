package br.com.ekklesia.ekklesia_admin_api.church;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "churches")
public class Church {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 14,unique = true)
    private String cnpj;

    @Column(length = 80)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
