package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shards")
@RequiredArgsConstructor
public class ShardController {
    private final ShardService shardService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public void createShard(@RequestBody ShardCreationDto dto) {
        shardService.createShard(dto);
    }

    @GetMapping()
    public List<ShardDto> getUserShards() {
        return shardService.getUserShards();
    }

    @GetMapping("/{shardId}")
    public ShardDto getShard(@PathVariable Long shardId) {
        return shardService.getShardById(shardId);
    }

    @DeleteMapping("/{shardId}")
    public void deleteShard(@PathVariable Long shardId) {
        shardService.deleteShard(shardId);
    }

    @PutMapping("/{shardId}")
    public void updateShardInfo(@PathVariable Long shardId,
                                @RequestBody ShardUpdateDto dto) {
        shardService.updateShardInfo(shardId, dto);
    }
}