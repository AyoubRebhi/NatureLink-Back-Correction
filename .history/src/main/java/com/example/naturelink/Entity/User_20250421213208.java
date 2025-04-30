package com.example.naturelink.Entity;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "username")
        }) // Changed table name to avoid reserved keyword conflict
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
=======

@Entity
@Table(name = "user")
public class User {
>>>>>>> origin/ayoub

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

<<<<<<< HEAD
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
    private boolean blocked;
    private String profilePic;
    private boolean enabled = false;
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING;
    private boolean isApproved = false;
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
=======
    private String username;
    private String email;
    private String password;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

>>>>>>> origin/ayoub
    public String getEmail() {
        return email;
    }

<<<<<<< HEAD

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled && isApproved;  // Only enabled if approved
    }
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean getEnabled() { return enabled; }
    public void setIsApproved(boolean isApproved) { this.isApproved = isApproved; }

}
=======
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
>>>>>>> origin/ayoub
