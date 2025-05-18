package eu.hyperspace.ftsapp.application.domain.entity;

import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "shard_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShardUser {

    @EmbeddedId
    private ShardUserId id;

    @ManyToOne
    @MapsId("shardId") // Связь с частью составного ключа
    @JoinColumn(name = "shard_id")
    private Shard shard;

    @Column(name = "access_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShardUserId implements Serializable {
        @Column(name = "shard_id")
        private Long shardId;

        @Column(name = "user_id")
        private Long userId;
    }
}
