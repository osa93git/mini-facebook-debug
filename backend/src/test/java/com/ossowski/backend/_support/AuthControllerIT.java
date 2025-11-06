package com.ossowski.backend._support;

import com.ossowski.backend.user.UserRepository;
import com.ossowski.backend.user.model.User;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT extends PostgresIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedUser(){
//        userRepository.deleteAll();
//        var u = new User();
//        u.setEmail("test@example.com");
//        u.setPassword(passwordEncoder.encode("password1"));
//        u.setEnabled(true);
////        u.setRole();
    }

}
