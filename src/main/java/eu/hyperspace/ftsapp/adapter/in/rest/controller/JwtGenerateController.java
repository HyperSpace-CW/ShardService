package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtGenerateController {
    private final JwtService jwtService;

    @GetMapping("/token/generate")
    public String generateToken() {
        return jwtService.generateJwtToken();
    }
}
