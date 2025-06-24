package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.adapter.in.rest.openapi.ShareTokenControllerApi;
import eu.hyperspace.ftsapp.application.domain.dto.sharetoken.ShareTokenDTO;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.service.ShareTokenService;
import eu.hyperspace.ftsapp.application.util.mapper.ShareTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shards/share")
@RequiredArgsConstructor
public class ShareTokenController implements ShareTokenControllerApi {

    private final ShareTokenService shareTokenService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{shardId}")
    public ShareTokenDTO createShareToken(
            @PathVariable Long shardId,
            @RequestParam AccessLevel accessLevel) {
        return shareTokenService.createToken(shardId, accessLevel);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{token}")
    public String readToken(
            @PathVariable String token
    ) {
        return shareTokenService.readToken(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{shardId}")
    public String revokeToken(
            @PathVariable Long shardId
    ) {
        return shareTokenService.revokeAccesses(shardId);
    }
}