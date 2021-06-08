package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto create(Long communityId, Member currentMember,
                   CreatePostDto createPostDto, List<MultipartFile> postImageFiles);

    void delete(Long communityId, Long postId, Member currentMember);

    PostDto findById(Long communityId, Long postId, Member currentMember);

    List<PostDto> findByAll(Long communityId, Member currentMember, Pageable pageable);
}
