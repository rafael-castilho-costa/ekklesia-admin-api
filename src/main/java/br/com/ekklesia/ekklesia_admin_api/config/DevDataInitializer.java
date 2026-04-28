package br.com.ekklesia.ekklesia_admin_api.config;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import br.com.ekklesia.ekklesia_admin_api.user.Role;
import br.com.ekklesia.ekklesia_admin_api.user.RoleRepository;
import br.com.ekklesia.ekklesia_admin_api.user.User;
import br.com.ekklesia.ekklesia_admin_api.user.UserRepository;
import br.com.ekklesia.ekklesia_admin_api.user.UserScope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer {

    private final ChurchRepository churchRepository;
    private final PersonaRepository personaRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:admin@ekklesia.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:123456}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.name:Administrador}")
    private String adminName;

    @Value("${app.bootstrap.admin.tax-id:12345678901}")
    private String adminTaxId;

    @Value("${app.bootstrap.church.name:Assembleia de Deus Missao Subsede Jardim Todos os Santos}")
    private String churchName;

    @Value("${app.bootstrap.church.cnpj:57166972000178}")
    private String churchCnpj;

    @Value("${app.bootstrap.church.city:Senador Canedo}")
    private String churchCity;

    @Value("${app.bootstrap.church.state:GO}")
    private String churchState;

    @Bean
    ApplicationRunner bootstrapDevData() {
        return args -> initialize();
    }

    @Transactional
    void initialize() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Church church = churchRepository.findByCnpj(churchCnpj)
                .orElseGet(this::createDefaultChurch);

        Role adminMasterRole = roleRepository.findByName("ROLE_ADMIN_MASTER")
                .orElseGet(() -> createRole("ROLE_ADMIN_MASTER"));

        Persona persona = new Persona();
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setTaxId(adminTaxId);
        persona.setName(adminName);
        persona.setEmail(adminEmail);
        persona = personaRepository.save(persona);

        User user = new User();
        user.setPersona(persona);
        user.setEmail(adminEmail);
        user.setPassword(passwordEncoder.encode(adminPassword));
        user.setActive(true);
        user.setScope(UserScope.PLATFORM);
        user.setChurch(null);
        user.setRoles(Set.of(adminMasterRole));
        userRepository.save(user);
    }

    private Church createDefaultChurch() {
        Church church = new Church();
        church.setName(churchName);
        church.setCnpj(churchCnpj);
        church.setCity(churchCity);
        church.setState(churchState);
        return churchRepository.save(church);
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }
}
