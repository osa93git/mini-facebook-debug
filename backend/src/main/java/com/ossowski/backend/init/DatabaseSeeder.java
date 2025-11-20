package com.ossowski.backend.init;

import com.ossowski.backend.comment.CommentRepository;
import com.ossowski.backend.comment.model.Comment;
import com.ossowski.backend.post.PostRepository;
import com.ossowski.backend.post.model.MediaType;
import com.ossowski.backend.post.model.Post;
import com.ossowski.backend.user.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ossowski.backend.user.model.User;
import com.ossowski.backend.user.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public DatabaseSeeder(PasswordEncoder passwordEncoder, UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {
            User piotr = new User(
                    "Piotr",
                    "Ossowski",
                    "piotr@example.com",
                        passwordEncoder.encode("haslo123")
            );

//            piotr.getRoles().add(Role.USER);

            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            piotr.setRoles(roles);

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



            Post post = new Post();
            post.setAuthor(piotr);
            post.setText("To jest pierwszy post Piotra");
            post.setMediaUrl(null);
            post.setMediaType(MediaType.NONE);

            Post post2 = new Post();
            post2.setAuthor(piotr);
            post2.setText("To jest drugi post Piotra");
            post2.setMediaUrl("post_url");
            post2.setMediaType(MediaType.NONE);

            postRepository.save(post);
            postRepository.save(post2);

            Comment comment = new Comment();
            comment.setAuthor(pawel);
            comment.setPost(post);
            comment.setContent("to jest komentarz pawla do posta piotra");

            commentRepository.save(comment);

        }
    }
}
