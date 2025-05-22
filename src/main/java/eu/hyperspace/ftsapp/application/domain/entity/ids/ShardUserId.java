package eu.hyperspace.ftsapp.application.domain.entity.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShardUserId implements Serializable {
    @Column(name = "shard_id")
    private Long shardId;

    @Column(name = "user_id")
    private Long userId;

    public static ShardUserId of(Long shardId, Long userId) {
        return new ShardUserId(shardId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShardUserId that = (ShardUserId) o;
        return Objects.equals(shardId, that.shardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shardId, userId);
    }
}
