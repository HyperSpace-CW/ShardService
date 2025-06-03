package eu.hyperspace.ftsapp.application.domain.entity;

import eu.hyperspace.ftsapp.application.domain.enums.ShardState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shard")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DynamicUpdate
public class Shard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private long ownerId;

    private long fileCount;

    private long totalSize;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShardState state = ShardState.ACTIVE;

    @OneToMany(mappedBy = "shard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SFile> files;


}
