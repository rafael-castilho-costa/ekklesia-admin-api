package br.com.ekklesia.ekklesia_admin_api.tenant;

import br.com.ekklesia.ekklesia_admin_api.exception.ApiError;
import br.com.ekklesia.ekklesia_admin_api.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String CHURCH_ID_HEADER = "X-Church-Id";

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public TenantFilter() {}

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails cud) {
                boolean adminRoute = request.getRequestURI().startsWith("/admin/");
                String headerValue = request.getHeader(CHURCH_ID_HEADER);

                if (headerValue == null || headerValue.isBlank()) {
                    if (cud.isPlatformAdmin() && adminRoute) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    writeError(
                            response,
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request",
                            "Header X-Church-Id is required.",
                            request.getRequestURI()
                    );
                    return;
                }

                Long churchId;
                try {
                    churchId = Long.valueOf(headerValue);
                } catch (NumberFormatException ex) {
                    writeError(
                            response,
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Bad Request",
                            "Header X-Church-Id is invalid.",
                            request.getRequestURI()
                    );
                    return;
                }

                if (!cud.isPlatformAdmin() && !churchId.equals(cud.getChurchId())) {
                    writeError(
                            response,
                            HttpServletResponse.SC_FORBIDDEN,
                            "Forbidden",
                            "Access denied for the informed church.",
                            request.getRequestURI()
                    );
                    return;
                }

                TenantContext.setChurchId(churchId);
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String path
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                new ApiError(LocalDateTime.now(), status, error, message, path)
        );
    }
}
