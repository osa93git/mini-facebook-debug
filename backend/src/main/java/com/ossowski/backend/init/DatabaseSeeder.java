package com.ossowski.backend.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ossowski.backend.user.User;
import com.ossowski.backend.user.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;

    public DatabaseSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    

    @Override
    public void run(String... args){
        if(userRepository.count() == 0){
            User piotr = new User(
                "Piotr",
                "Ossowski",
                "piotr@example.com",
                "haslo123"
            );
            userRepository.save(piotr);
        }
    }
}
