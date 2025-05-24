package eu.hyperspace.ftsapp.security.service;

import eu.hyperspace.ftsapp.security.authentication.AuthenticatedUser;

public interface JwtService {
    boolean validateJwtToken(String token);

    AuthenticatedUser getUserFromToken(String token);

    String generateJwtToken();
}
