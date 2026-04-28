package br.com.ekklesia.ekklesia_admin_api.administration.user;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import br.com.ekklesia.ekklesia_admin_api.exception.BusinessException;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import br.com.ekklesia.ekklesia_admin_api.user.Role;
import br.com.ekklesia.ekklesia_admin_api.user.RoleRepository;
import br.com.ekklesia.ekklesia_admin_api.user.User;
import br.com.ekklesia.ekklesia_admin_api.user.UserRepository;
import br.com.ekklesia.ekklesia_admin_api.user.UserScope;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChurchRepository churchRepository;
    private final PersonaRepository personaRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    public List<AdminUserResponse> list(Long churchId) {
        List<User> users = churchId == null
                ? userRepository.findAllByOrderByIdAsc()
                : userRepository.findAllByChurchIdOrderByIdAsc(churchId);

        return users.stream().map(AdminUserResponse::from).toList();
    }

    public AdminUserResponse getById(Integer id) {
        return AdminUserResponse.from(findUser(id));
    }

    public AdminUserResponse create(AdminUserCreateRequest request) {
        validateCreateRequest(request);

        User user = new User();
        Persona persona = new Persona();
        Church church = findChurchForScope(request.scope(), request.churchId());

        applyPersona(persona, request, church);
        persona = personaRepository.save(persona);
        applyUser(user, request.scope(), church, request.email(), request.password(), request.active(), request.roles());
        user.setPersona(persona);

        User savedUser = userRepository.save(user);
        auditLogService.register("User", savedUser.getId(), AuditAction.CREATE, "Usuario criado pelo modulo Administracao.");
        return AdminUserResponse.from(savedUser);
    }

    public AdminUserResponse update(Integer id, AdminUserUpdateRequest request) {
        User user = findUser(id);
        UserScope scope = request.scope() != null ? request.scope() : user.getScope();
        Long churchId = resolveChurchId(request.churchId(), user);
        Church church = findChurchForScope(scope, churchId);

        validateUpdateRequest(user, request, scope, churchId);

        user.setScope(scope);
        user.setChurch(church);
        user.getPersona().setChurch(church);

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }
        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(resolveRoles(request.roles()));
        }

        User updatedUser = userRepository.save(user);
        auditLogService.register("User", updatedUser.getId(), AuditAction.UPDATE, "Usuario atualizado pelo modulo Administracao.");
        return AdminUserResponse.from(updatedUser);
    }

    public AdminUserResponse setActive(Integer id, boolean active) {
        User user = findUser(id);

        if (!active && hasAdminMasterRole(user) && countActiveAdminMasters() <= 1) {
            throw new BusinessException("Nao e permitido bloquear o ultimo admin master ativo.");
        }

        user.setActive(active);
        User updatedUser = userRepository.save(user);
        auditLogService.register("User", updatedUser.getId(), AuditAction.UPDATE, active
                ? "Usuario desbloqueado pelo modulo Administracao."
                : "Usuario bloqueado pelo modulo Administracao.");
        return AdminUserResponse.from(updatedUser);
    }

    public void delete(Integer id) {
        User user = findUser(id);

        if (hasAdminMasterRole(user) && countAdminMasters() <= 1) {
            throw new BusinessException("Nao e permitido excluir o ultimo admin master.");
        }

        userRepository.delete(user);
        auditLogService.register("User", id, AuditAction.DELETE, "Usuario removido pelo modulo Administracao.");
    }

    private User findUser(Integer id) {
        return userRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado."));
    }

    private void validateCreateRequest(AdminUserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe um usuario com o email informado.");
        }
        validateScopeAndRoles(request.scope(), request.churchId(), request.roles());
    }

    private void validateUpdateRequest(User user, AdminUserUpdateRequest request, UserScope scope, Long churchId) {
        if (request.email() != null && userRepository.existsByEmailAndIdNot(request.email(), user.getId())) {
            throw new BusinessException("Ja existe um usuario com o email informado.");
        }

        Set<String> roles = request.roles() != null && !request.roles().isEmpty()
                ? request.roles()
                : user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());

        validateScopeAndRoles(scope, churchId, roles);
    }

    private Long resolveChurchId(Long requestedChurchId, User user) {
        if (requestedChurchId != null) {
            return requestedChurchId;
        }

        return user.getChurch() != null ? user.getChurch().getId() : null;
    }

    private void validateScopeAndRoles(UserScope scope, Long churchId, Set<String> roleNames) {
        if (scope == UserScope.TENANT && churchId == null) {
            throw new BusinessException("Usuarios TENANT devem informar a igreja.");
        }

        if (scope == UserScope.PLATFORM && churchId != null) {
            throw new BusinessException("Usuarios PLATFORM nao podem ser vinculados a uma igreja.");
        }

        if (roleNames.contains("ROLE_ADMIN_MASTER") && scope != UserScope.PLATFORM) {
            throw new BusinessException("ROLE_ADMIN_MASTER exige usuario com escopo PLATFORM.");
        }
    }

    private Church findChurchForScope(UserScope scope, Long churchId) {
        if (scope == UserScope.PLATFORM) {
            return null;
        }

        if (churchId == null) {
            throw new BusinessException("Igreja nao informada para usuario TENANT.");
        }

        return churchRepository.findById(churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Igreja nao encontrada."));
    }

    private void applyPersona(Persona persona, AdminUserCreateRequest request, Church church) {
        persona.setChurch(church);
        persona.setPersonaType(request.personaType() != null ? request.personaType() : PersonaType.NATURAL_PERSON);
        persona.setTaxId(request.taxId());
        persona.setName(request.name());
        persona.setBirthDate(request.birthDate());
        persona.setMaritalStatus(request.maritalStatus());
        persona.setPhone(request.phone());
        persona.setEmail(request.personaEmail() != null ? request.personaEmail() : request.email());
        persona.setAddress(request.address());
    }

    private void applyUser(
            User user,
            UserScope scope,
            Church church,
            String email,
            String password,
            Boolean active,
            Set<String> roleNames
    ) {
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(active == null || active);
        user.setScope(scope);
        user.setChurch(church);
        user.setRoles(resolveRoles(roleNames));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        List<Role> roles = roleRepository.findByNameIn(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new BusinessException("Uma ou mais roles informadas nao existem.");
        }

        return new HashSet<>(roles);
    }

    private boolean hasAdminMasterRole(User user) {
        return user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN_MASTER".equals(role.getName()));
    }

    private long countAdminMasters() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .filter(this::hasAdminMasterRole)
                .count();
    }

    private long countActiveAdminMasters() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .filter(User::isActive)
                .filter(this::hasAdminMasterRole)
                .count();
    }
}
