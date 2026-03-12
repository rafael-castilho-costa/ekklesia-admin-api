package br.com.ekklesia.ekklesia_admin_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rbac")
public class RbacTestController {
    // Apenas ADMIN
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "OK - ADMIN";
    }

    // Apenas ADMIN e TREASURER
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TREASURER')")
    @GetMapping("/finance")
    public String finance() {
        return "OK - FINANCE";
    }

    // Apenas ADMIN e SECRETARY
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARY')")
    @GetMapping("/secretary")
    public String secretary() {
        return "OK - SECRETARY";
    }

    // Apenas ADMIN e TEATCHER
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TEACHER')")
    @GetMapping("/teatcher")
    public String teatcher() {
        return "OK - TEATCHER";
    }
}
