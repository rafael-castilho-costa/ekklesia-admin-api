package br.com.ekklesia.ekklesia_admin_api.church;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {

    private final ChurchService churchService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARY')")
    public List<ChurchResponse> list() {
        return churchService.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARY')")
    public ChurchResponse getById(@PathVariable Long id) {
        return churchService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ChurchResponse> create(@Valid @RequestBody ChurchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(churchService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ChurchResponse update(@PathVariable Long id, @Valid @RequestBody ChurchRequest request) {
        return churchService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        churchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
