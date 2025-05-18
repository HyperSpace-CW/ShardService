package eu.hyperspace.ftsapp.security.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUser {
    private String id;
    private String email;
}
