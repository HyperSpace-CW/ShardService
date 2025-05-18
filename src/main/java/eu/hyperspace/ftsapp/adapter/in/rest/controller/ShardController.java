package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shards")
@RequiredArgsConstructor
public class ShardController {
    private final ShardService shardService;

    @GetMapping("/{shardId}")
    public ShardDto getShard(@PathVariable Long shardId) {
        return shardService.getShardById(shardId);
    }
    

}