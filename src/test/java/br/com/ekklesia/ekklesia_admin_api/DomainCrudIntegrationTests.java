package br.com.ekklesia.ekklesia_admin_api;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member.Member;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member.MemberRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.Ministry;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.StatusMember;
import br.com.ekklesia.ekklesia_admin_api.finance.FinanceTransactionRepository;
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

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private FinanceTransactionRepository financeTransactionRepository;

    private Long churchOneId;
    private Long churchTwoId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        memberRepository.deleteAll();
        personaRepository.deleteAll();
        financeTransactionRepository.deleteAll();
        churchRepository.deleteAll();

        Church churchOne = createChurch("Igreja Base", "12345678000199");
        Church churchTwo = createChurch("Igreja Vizinha", "98765432000155");
        churchOneId = churchOne.getId();
        churchTwoId = churchTwo.getId();

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role treasurerRole = new Role();
        treasurerRole.setName("ROLE_TREASURER");
        treasurerRole = roleRepository.save(treasurerRole);

        createUser(churchOne, adminRole, "admin1@ekklesia.com", "123456", "Administrador Um", "12345678901");
        createUser(churchTwo, adminRole, "admin2@ekklesia.com", "123456", "Administrador Dois", "10987654321");
        createUser(churchOne, treasurerRole, "tesouraria1@ekklesia.com", "123456", "Tesouraria Um", "12345678902");
    }

    @Test
    void shouldCreateAndManagePersonaAndMemberUsingTenantHeader() throws Exception {
        String token = loginAndGetToken("admin1@ekklesia.com", "123456");

        String personaResponse = mockMvc.perform(post("/personas")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "personaType": "NATURAL_PERSON",
                                  "taxId": "98765432100",
                                  "name": "Maria Silva",
                                  "birthDate": "1990-01-02",
                                  "maritalStatus": "CASADO",
                                  "email": "maria@ekklesia.com",
                                  "phone": "62999999999",
                                  "address": "Rua A"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.churchId").value(churchOneId))
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String personaId = extractNumericField(personaResponse, "id");

        String memberResponse = mockMvc.perform(post("/members")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "personaId": %s,
                                  "membershipDate": "2026-04-10",
                                  "baptismDate": "2020-01-15",
                                  "baptized": true,
                                  "ministry": "WOMEN",
                                  "statusMember": "ACTIVE",
                                  "notes": "Membro cadastrado via teste"
                                }
                                """.formatted(personaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.personaId").value(Integer.parseInt(personaId)))
                .andExpect(jsonPath("$.persona.id").value(Integer.parseInt(personaId)))
                .andExpect(jsonPath("$.persona.churchId").value(churchOneId))
                .andExpect(jsonPath("$.persona.name").value("Maria Silva"))
                .andExpect(jsonPath("$.statusMember").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String memberId = extractNumericField(memberResponse, "id");

        mockMvc.perform(get("/members")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .param("statusMember", "ACTIVE")
                        .param("search", "Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Integer.parseInt(memberId)))
                .andExpect(jsonPath("$[0].persona.name").value("Maria Silva"));

        mockMvc.perform(get("/members/{id}", memberId)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persona.email").value("maria@ekklesia.com"))
                .andExpect(jsonPath("$.ministry").value("WOMEN"));

        mockMvc.perform(put("/members/{id}", memberId)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "personaId": %s,
                                  "membershipDate": "2026-04-10",
                                  "baptismDate": "2020-01-15",
                                  "baptized": true,
                                  "ministry": "MEN",
                                  "statusMember": "AWAY",
                                  "notes": "Atualizado"
                                }
                                """.formatted(personaId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ministry").value("MEN"))
                .andExpect(jsonPath("$.statusMember").value("AWAY"))
                .andExpect(jsonPath("$.notes").value("Atualizado"));

        mockMvc.perform(delete("/members/{id}", memberId)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/members/{id}", memberId)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotLeakPersonasOrMembersAcrossChurches() throws Exception {
        String tokenChurchOne = loginAndGetToken("admin1@ekklesia.com", "123456");
        String tokenChurchTwo = loginAndGetToken("admin2@ekklesia.com", "123456");

        Persona churchOnePersona = createPersona(churchOneId, "Pessoa Igreja Um", "11111111111");
        Member churchOneMember = createMember(churchOnePersona, Ministry.WOMEN, StatusMember.ACTIVE);

        Persona churchTwoPersona = createPersona(churchTwoId, "Pessoa Igreja Dois", "22222222222");
        createMember(churchTwoPersona, Ministry.MEN, StatusMember.ACTIVE);

        mockMvc.perform(get("/personas")
                        .header("Authorization", "Bearer " + tokenChurchOne)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Pessoa Igreja Um')]").exists())
                .andExpect(jsonPath("$[?(@.name=='Pessoa Igreja Dois')]").doesNotExist());

        mockMvc.perform(get("/members")
                        .header("Authorization", "Bearer " + tokenChurchOne)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].persona.name").value("Pessoa Igreja Um"));

        mockMvc.perform(get("/members/{id}", churchOneMember.getId())
                        .header("Authorization", "Bearer " + tokenChurchTwo)
                        .header("X-Church-Id", churchTwoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectBodyChurchIdDifferentFromTenantHeader() throws Exception {
        String token = loginAndGetToken("admin1@ekklesia.com", "123456");

        mockMvc.perform(post("/personas")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "churchId": %s,
                                  "personaType": "NATURAL_PERSON",
                                  "taxId": "33333333333",
                                  "name": "Pessoa Invalida"
                                }
                                """.formatted(churchTwoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("churchId do corpo difere do header X-Church-Id."));
    }

    @Test
    void shouldCreateAndListFinanceTransactionsUsingTenantHeader() throws Exception {
        String token = loginAndGetToken("admin1@ekklesia.com", "123456");

        String createdResponse = mockMvc.perform(post("/finance/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "INCOME",
                                  "description": "Dizimos do mes",
                                  "category": "Dizimos",
                                  "paymentMethod": "PIX",
                                  "amount": 15800,
                                  "transactionDate": "2026-02-27",
                                  "notes": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.description").value("Dizimos do mes"))
                .andExpect(jsonPath("$.category").value("Dizimos"))
                .andExpect(jsonPath("$.paymentMethod").value("PIX"))
                .andExpect(jsonPath("$.amount").value(15800))
                .andExpect(jsonPath("$.transactionDate").value("2026-02-27"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String createdId = extractNumericField(createdResponse, "id");

        mockMvc.perform(post("/finance/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "EXPENSE",
                                  "description": "Conta de energia",
                                  "category": "Infraestrutura",
                                  "paymentMethod": "BOLETO",
                                  "amount": 900,
                                  "transactionDate": "2026-01-15",
                                  "notes": "Janeiro"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/finance/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .param("type", "INCOME")
                        .param("category", "Dizimos")
                        .param("paymentMethod", "PIX")
                        .param("startDate", "2026-02-01")
                        .param("endDate", "2026-02-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(Integer.parseInt(createdId)))
                .andExpect(jsonPath("$[0].description").value("Dizimos do mes"))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    void shouldNotLeakFinanceTransactionsAcrossChurches() throws Exception {
        String tokenChurchOne = loginAndGetToken("admin1@ekklesia.com", "123456");
        String tokenChurchTwo = loginAndGetToken("admin2@ekklesia.com", "123456");

        mockMvc.perform(post("/finance/transactions")
                        .header("Authorization", "Bearer " + tokenChurchOne)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "INCOME",
                                  "description": "Oferta Igreja Um",
                                  "category": "Ofertas",
                                  "paymentMethod": "PIX",
                                  "amount": 1000,
                                  "transactionDate": "2026-03-10",
                                  "notes": null
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/finance/transactions")
                        .header("Authorization", "Bearer " + tokenChurchTwo)
                        .header("X-Church-Id", churchTwoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "INCOME",
                                  "description": "Oferta Igreja Dois",
                                  "category": "Ofertas",
                                  "paymentMethod": "DINHEIRO",
                                  "amount": 2000,
                                  "transactionDate": "2026-03-11",
                                  "notes": null
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/finance/transactions")
                        .header("Authorization", "Bearer " + tokenChurchOne)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.description=='Oferta Igreja Um')]").exists())
                .andExpect(jsonPath("$[?(@.description=='Oferta Igreja Dois')]").doesNotExist());
    }

    @Test
    void shouldAllowTreasurerToCreateAndListFinanceTransactions() throws Exception {
        String token = loginAndGetToken("tesouraria1@ekklesia.com", "123456");

        mockMvc.perform(post("/finance/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "INCOME",
                                  "description": "Oferta Tesouraria",
                                  "category": "Ofertas",
                                  "paymentMethod": "PIX",
                                  "amount": 1200,
                                  "transactionDate": "2026-04-29",
                                  "notes": null
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/finance/transactions")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Church-Id", churchOneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.description=='Oferta Tesouraria')]").exists());
    }

    private Church createChurch(String name, String cnpj) {
        Church church = new Church();
        church.setName(name);
        church.setCnpj(cnpj);
        church.setCity("Goiania");
        church.setState("GO");
        return churchRepository.save(church);
    }

    private void createUser(Church church, Role role, String email, String password, String name, String taxId) {
        Persona persona = new Persona();
        persona.setChurch(church);
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setName(name);
        persona.setTaxId(taxId);
        persona = personaRepository.save(persona);

        User user = new User();
        user.setPersona(persona);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    private Persona createPersona(Long churchId, String name, String taxId) {
        Church church = churchRepository.findById(churchId).orElseThrow();
        Persona persona = new Persona();
        persona.setChurch(church);
        persona.setPersonaType(PersonaType.NATURAL_PERSON);
        persona.setName(name);
        persona.setTaxId(taxId);
        return personaRepository.save(persona);
    }

    private Member createMember(Persona persona, Ministry ministry, StatusMember statusMember) {
        Member member = new Member();
        member.setPersona(persona);
        member.setMembershipDate(LocalDate.of(2026, 4, 10));
        member.setBaptized(true);
        member.setMinistry(ministry);
        member.setStatusMember(statusMember);
        member.setNotes("Seed");
        return memberRepository.save(member);
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
