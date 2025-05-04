package com.ossowski.backend.user;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "users", uniqueConstraints= @UniqueConstraint(columnNames="email"))
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue
    @Getter
    private UUID id;

    @Getter @Setter
    @Column(nullable = false)
    private String firstName;

    @Getter @Setter
    @Column(nullable = false)
    private String lastName;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter @Setter
    @Column(nullable = false)
    private String password;

    @Getter @Setter
    @Column(nullable = false)
    private String profilePhotoUrl;
    
    @Getter @Setter
    @Column(nullable = false, length = 500)
    private String bio;


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
}
