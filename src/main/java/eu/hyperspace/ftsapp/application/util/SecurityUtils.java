package eu.hyperspace.ftsapp.application.util;

import eu.hyperspace.ftsapp.security.authentication.SecurityUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public SecurityUserDetails getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof SecurityUserDetails)) {
            throw new IllegalStateException(
                    "Principal is not CustomUserDetails");
        }

        return (SecurityUserDetails) authentication.getPrincipal();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getUsername();
    }

    public Long getCurrentUserId() {
        return Long.valueOf(getCurrentUser().getUsername());
    }
}
