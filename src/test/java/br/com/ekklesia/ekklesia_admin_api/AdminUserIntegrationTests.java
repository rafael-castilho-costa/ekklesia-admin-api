package br.com.ekklesia.ekklesia_admin_api;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ChurchRepository churchRepository;

    private Long churchId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        personaRepository.deleteAll();
        churchRepository.deleteAll();

        Church church = new Church();
        church.setName("Igreja Teste");
        church.setCnpj("12345678000199");
        church.setCity("Goiania");
        church.setState("GO");
        church = churchRepository.save(church);
        churchId = church.getId();

        Role adminMasterRole = createRole("ROLE_ADMIN_MASTER");
        Role adminRole = createRole("ROLE_ADMIN");

        createPlatformAdminMaster(adminMasterRole, "master@ekklesia.com", "123456");
        createTenantAdmin(church, adminRole, "admin@ekklesia.com", "123456");
    }

    @Test
    void shouldCreateAdminUserWithoutPersonaTypeUsingNaturalPersonAsDefault() throws Exception {
        String token = loginAndGetToken("master@ekklesia.com", "123456");

        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "churchId": %s,
                                  "scope": "TENANT",
                                  "taxId": "98765432100",
                                  "name": "Novo Administrador",
                                  "email": "novo.admin@ekklesia.com",
                                  "password": "123456",
                                  "roles": ["ROLE_ADMIN"]
                                }
                                """.formatted(churchId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Novo Administrador"))
                .andExpect(jsonPath("$.personaName").value("Novo Administrador"))
                .andExpect(jsonPath("$.email").value("novo.admin@ekklesia.com"))
                .andExpect(jsonPath("$.scope").value("TENANT"))
                .andExpect(jsonPath("$.churchId").value(churchId))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        User savedUser = userRepository.findByEmail("novo.admin@ekklesia.com")
                .flatMap(user -> userRepository.findDetailedById(user.getId()))
                .orElseThrow();

        assertEquals(UserScope.TENANT, savedUser.getScope());
        assertNotNull(savedUser.getPersona());
        assertEquals(PersonaType.NATURAL_PERSON, savedUser.getPersona().getPersonaType());
    }

    @Test
    void shouldUpdateAdminUserName() throws Exception {
        String token = loginAndGetToken("master@ekklesia.com", "123456");
        User user = userRepository.findByEmail("admin@ekklesia.com").orElseThrow();

        mockMvc.perform(put("/admin/users/{id}", user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "churchId": %s,
                                  "scope": "TENANT",
                                  "name": "Administrador Editado",
                                  "email": "admin@ekklesia.com",
                                  "roles": ["ROLE_ADMIN"]
                                }
                                """.formatted(churchId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Administrador Editado"))
                .andExpect(jsonPath("$.personaName").value("Administrador Editado"));
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private void createPlatformAdminMaster(Role role, String email, String rawPassword) {
        Persona persona = new Persona();
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setName(email);
        persona.setTaxId(generateTaxIdFromEmail(email));
        persona = personaRepository.save(persona);

        User user = new User();
        user.setPersona(persona);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        user.setScope(UserScope.PLATFORM);
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    private void createTenantAdmin(Church church, Role role, String email, String rawPassword) {
        Persona persona = new Persona();
        persona.setChurch(church);
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setName(email);
        persona.setTaxId(generateTaxIdFromEmail(email));
        persona = personaRepository.save(persona);

        User user = new User();
        user.setPersona(persona);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        user.setScope(UserScope.TENANT);
        user.setChurch(church);
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractToken(responseBody);
    }

    private String extractToken(String responseBody) {
        String prefix = "\"token\":\"";
        int start = responseBody.indexOf(prefix);
        if (start < 0) {
            throw new IllegalStateException("Token nao encontrado na resposta: " + responseBody);
        }

        int tokenStart = start + prefix.length();
        int tokenEnd = responseBody.indexOf('"', tokenStart);
        if (tokenEnd < 0) {
            throw new IllegalStateException("Resposta de token invalida: " + responseBody);
        }

        return responseBody.substring(tokenStart, tokenEnd);
    }

    private String generateTaxIdFromEmail(String email) {
        String digits = email.replaceAll("\\D", "");
        return (digits + "00000000000000").substring(0, 14);
    }
}
