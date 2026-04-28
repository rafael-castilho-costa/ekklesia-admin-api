package br.com.ekklesia.ekklesia_admin_api.administration.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN_MASTER')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<AdminUserResponse> list(@RequestParam(required = false) Long churchId) {
        return adminUserService.list(churchId);
    }

    @GetMapping("/{id}")
    public AdminUserResponse getById(@PathVariable Integer id) {
        return adminUserService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> create(@Valid @RequestBody AdminUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.create(request));
    }

    @PutMapping("/{id}")
    public AdminUserResponse update(@PathVariable Integer id, @Valid @RequestBody AdminUserUpdateRequest request) {
        return adminUserService.update(id, request);
    }

    @PatchMapping("/{id}/block")
    public AdminUserResponse block(@PathVariable Integer id) {
        return adminUserService.setActive(id, false);
    }

    @PatchMapping("/{id}/unblock")
    public AdminUserResponse unblock(@PathVariable Integer id) {
        return adminUserService.setActive(id, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        adminUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
