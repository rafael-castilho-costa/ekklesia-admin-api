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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTests {

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

        Role adminRole = createRole("ROLE_ADMIN");
        Role secretaryRole = createRole("ROLE_SECRETARY");

        createUser(church, adminRole, "admin@ekklesia.com", "123456");
        createUser(church, secretaryRole, "secretary@ekklesia.com", "123456");
    }

    @Test
    void shouldLoginAndReturnJwtToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@ekklesia.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@ekklesia.com",
                                  "password": "senha-errada"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRequireTenantHeaderForProtectedEndpoints() throws Exception {
        String token = loginAndGetToken("admin@ekklesia.com", "123456");

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Header X-Church-Id is required."));
    }

    @Test
    void shouldRejectAccessWhenTenantHeaderDoesNotMatchUserChurch() throws Exception {
        String token = loginAndGetToken("admin@ekklesia.com", "123456");

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchId + 1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied for the informed church."));
    }

    @Test
    void shouldReturnCurrentAuthenticatedUser() throws Exception {
        String token = loginAndGetToken("admin@ekklesia.com", "123456");

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@ekklesia.com"))
                .andExpect(jsonPath("$.churchId").value(churchId))
                .andExpect(jsonPath("$.churchName").value("Igreja Teste"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void shouldForbidSecretaryFromCreatingChurch() throws Exception {
        String token = loginAndGetToken("secretary@ekklesia.com", "123456");

        mockMvc.perform(post("/churches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Nova Igreja",
                                  "cnpj": "55555555000155",
                                  "city": "Goiania",
                                  "state": "GO"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldExposeEnumMetadataForFrontend() throws Exception {
        String token = loginAndGetToken("admin@ekklesia.com", "123456");

        mockMvc.perform(get("/metadata/enums/member-statuses")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").exists())
                .andExpect(jsonPath("$[0].description").exists());
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

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private void createUser(Church church, Role role, String email, String rawPassword) {
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
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    private String generateTaxIdFromEmail(String email) {
        String digits = email.replaceAll("\\D", "");
        return (digits + "00000000000000").substring(0, 14);
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
}
