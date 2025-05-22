package com.ossowski.backend.post.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ossowski.backend.comment.model.Comment;
import com.ossowski.backend.user.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private String mediaUrl;

    @Column(nullable = false)
    @Getter
    @Setter
    private MediaType mediaType;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @Getter
    @Setter
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Equality is based on non-null UUID. This is the recommended pattern for
     * JPA entities to avoid issues with lazy-loaded fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id != null && id.equals(post.id);
    }

    /**
     * Constant hash code strategy recommended for JPA entities. See:
     * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
