package com.example.naturelink.Service;

import com.example.naturelink.Entity.Post;
import com.example.naturelink.Entity.User;
import com.example.naturelink.repository.IPostRepository;
import com.example.naturelink.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final IPostRepository postRepository;
    private final IUserRepository userRepository;

    // Créer un post
    @Transactional

    public Post createPost(Post post, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        post.setUser(user);
        return postRepository.save(post);
    }

    // Récupérer tous les posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Mettre à jour un post
    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setContent(postDetails.getContent());
        return postRepository.save(post);
    }

    // Supprimer un post
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}