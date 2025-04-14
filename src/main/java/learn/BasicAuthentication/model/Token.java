package learn.BasicAuthentication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tokenId;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "token_created_at", nullable = false)
    private LocalDateTime tokenCreatedAt;

    @Column(name = "token_expires_at", nullable = false)
    private LocalDateTime tokenExpiresAt;

    @Column(name = "token_validated_at", nullable = true)
    private LocalDateTime tokenValidatedAt;

    @ManyToOne
    @JoinColumn(name = "token_account", nullable = false, referencedColumnName = "accountId")
    private Account tokenAccount;
}

