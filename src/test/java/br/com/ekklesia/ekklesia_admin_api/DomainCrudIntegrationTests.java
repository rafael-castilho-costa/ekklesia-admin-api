package br.com.ekklesia.ekklesia_admin_api;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member.MemberRepository;
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
class DomainCrudIntegrationTests {

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
    private MemberRepository memberRepository;

    @Autowired
    private ChurchRepository churchRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        memberRepository.deleteAll();
        personaRepository.deleteAll();
        churchRepository.deleteAll();

        Church church = new Church();
        church.setName("Igreja Base");
        church.setCnpj("12345678000199");
        church.setCity("Goiania");
        church.setState("GO");
        church = churchRepository.save(church);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Persona persona = new Persona();
        persona.setChurch(church);
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setName("Administrador");
        persona.setTaxId("12345678901");
        persona = personaRepository.save(persona);

        User user = new User();
        user.setPersona(persona);
        user.setEmail("admin@ekklesia.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setActive(true);
        user.setRoles(Set.of(adminRole));
        userRepository.save(user);
    }

    @Test
    void shouldCreateChurchPersonaAndMemberFlow() throws Exception {
        String token = loginAndGetToken("admin@ekklesia.com", "123456");

        String churchResponse = mockMvc.perform(post("/churches")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Igreja Centro",
                                  "cnpj": "98765432000155",
                                  "city": "Senador Canedo",
                                  "state": "GO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Igreja Centro"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String churchId = extractNumericField(churchResponse, "id");

        String personaResponse = mockMvc.perform(post("/personas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "churchId": %s,
                                  "personaType": "NATURAL_PERSON",
                                  "taxId": "98765432100",
                                  "name": "Maria Silva",
                                  "email": "maria@ekklesia.com",
                                  "phone": "62999999999",
                                  "address": "Rua A"
                                }
                                """.formatted(churchId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String personaId = extractNumericField(personaResponse, "id");

        mockMvc.perform(post("/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "personaId": %s,
                                  "membershipDate": "2026-04-10",
                                  "baptized": true,
                                  "ministry": "WOMEN",
                                  "statusMember": "ACTIVE",
                                  "notes": "Membro cadastrado via teste"
                                }
                                """.formatted(personaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.personaName").value("Maria Silva"))
                .andExpect(jsonPath("$.churchName").value("Igreja Centro"));

        mockMvc.perform(get("/personas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Maria Silva')]").exists());

        mockMvc.perform(get("/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].personaName").value("Maria Silva"));
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

        return extractStringField(responseBody, "token");
    }

    private String extractNumericField(String responseBody, String field) {
        String prefix = "\"" + field + "\":";
        int start = responseBody.indexOf(prefix);
        if (start < 0) {
            throw new IllegalStateException("Campo nao encontrado: " + field + " em " + responseBody);
        }

        int valueStart = start + prefix.length();
        int valueEnd = responseBody.indexOf(',', valueStart);
        if (valueEnd < 0) {
            valueEnd = responseBody.indexOf('}', valueStart);
        }
        return responseBody.substring(valueStart, valueEnd).trim();
    }

    private String extractStringField(String responseBody, String field) {
        String prefix = "\"" + field + "\":\"";
        int start = responseBody.indexOf(prefix);
        if (start < 0) {
            throw new IllegalStateException("Campo nao encontrado: " + field + " em " + responseBody);
        }

        int valueStart = start + prefix.length();
        int valueEnd = responseBody.indexOf('"', valueStart);
        return responseBody.substring(valueStart, valueEnd);
    }
}
