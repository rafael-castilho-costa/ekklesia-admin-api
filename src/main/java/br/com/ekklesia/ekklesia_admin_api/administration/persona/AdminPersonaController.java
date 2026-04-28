package br.com.ekklesia.ekklesia_admin_api.administration.persona;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRequest;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/personas")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN_MASTER')")
public class AdminPersonaController {

    private final AdminPersonaService adminPersonaService;

    @GetMapping
    public List<PersonaResponse> list(@RequestParam(required = false) Long churchId) {
        return adminPersonaService.list(churchId);
    }

    @GetMapping("/{id}")
    public PersonaResponse getById(@PathVariable Integer id) {
        return adminPersonaService.getById(id);
    }

    @PostMapping
    public ResponseEntity<PersonaResponse> create(@Valid @RequestBody PersonaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminPersonaService.create(request));
    }

    @PutMapping("/{id}")
    public PersonaResponse update(@PathVariable Integer id, @Valid @RequestBody PersonaRequest request) {
        return adminPersonaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        adminPersonaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
