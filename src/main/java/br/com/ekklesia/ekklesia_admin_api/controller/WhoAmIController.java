package br.com.ekklesia.ekklesia_admin_api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class WhoAmIController {

    @GetMapping("/whoami")
    public Object whoami(Authentication auth) {
        return new Object() {
            public final String name = auth.getName();
            public final Object authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        };
    }
}
