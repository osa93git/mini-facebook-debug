package com.ossowski.backend.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ossowski.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    //all public users
    List<User> findByProfilePublicTrue();


    // search public users using first/lastName
    List<User> findByProfilePublicTrueAndFirstNameContainingIgnoreCaseOrProfilePublicTrueAndLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );
}
