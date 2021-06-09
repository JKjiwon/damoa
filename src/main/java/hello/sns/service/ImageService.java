package hello.sns.service;

import hello.sns.entity.post.Post;
import hello.sns.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void deletePostAllImages(Long postId) {
        imageRepository.deleteByPost(postId);
    }
}
