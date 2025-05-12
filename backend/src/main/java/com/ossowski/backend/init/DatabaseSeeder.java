package com.ossowski.backend.init;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ossowski.backend.user.User;
import com.ossowski.backend.user.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    
    

    @Override
    public void run(String... args){
       
        if(userRepository.count() == 0){
            User piotr = new User(
                "Piotr",
                "Ossowski",
                "piotr@example.com",
                passwordEncoder.encode("haslo123")
            );
            User pawel = new User(
                "Pawel",
                "Ossowski",
                "pawel@example.com",
                passwordEncoder.encode("haslo1234")
            );
            User piotr2 = new User(
                "Jaroslaw",
                "Jaroslawski",
                "jar@war.com",
                passwordEncoder.encode("haslo12345")
            );
            userRepository.save(piotr);
            userRepository.save(pawel);
            userRepository.save(piotr2);
        }
    }
}
