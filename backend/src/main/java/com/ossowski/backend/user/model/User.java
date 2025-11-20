package com.ossowski.backend.user.model;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "email"
        ))
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String profilePhotoUrl;

    @Column(nullable = false, length = 500)
    private String bio;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Role role = Role.USER;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})
    )
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();


    @Column(nullable = false)
    private boolean profilePublic = false;

    //switch for false when email link activation will be done
    @Column(nullable = false)
    private boolean enabled = true;


    public User(UUID id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.profilePhotoUrl = generateDefaultPhotoUrl(email);
        this.bio = "";
    }


    public User(String firstName, String lastName, String email, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.profilePhotoUrl = generateDefaultPhotoUrl(email);
        this.bio = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(id, user.id);
    }

    public void setRoles(Set<Role> roles) {

        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.clear();
        if(roles != null) {
            this.roles.addAll(roles);
        }
    }

    private String generateDefaultPhotoUrl(String seed) {
        return "https://api.dicebear.com/6.x/identicon/svg?seed=" + seed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority( "ROLE_" + role.name())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

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
        return enabled;
    }

}
