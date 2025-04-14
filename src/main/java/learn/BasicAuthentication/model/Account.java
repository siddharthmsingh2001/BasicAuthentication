package learn.BasicAuthentication.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="account")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "account_email", unique = true, nullable = false)
    private String accountEmail;

    @Column(name = "account_password", nullable = false)
    private String accountPassword;

    @Column(name = "account_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role accountRole;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked;

    @Column(name = "account_enabled", nullable = false)
    private boolean accountEnabled;

    @CreatedDate
    @Column(name = "account_created_at")
    private LocalDateTime accountCreatedAt;

    @LastModifiedDate
    @Column(name = "account_updated_at")
    private LocalDateTime accountUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "account_admin", referencedColumnName = "accountId")
    private Account accountAdmin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_"+ accountRole.name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return accountPassword;
    }

    @Override
    public String getUsername() {
        return accountEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountEnabled;
    }

    @Override
    public String getName() {
        return accountEmail;
    }
}
