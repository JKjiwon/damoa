package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.post.CreatePostDto;
import hello.sns.web.dto.post.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto create(Long communityId, Member currentMember,
                   CreatePostDto createPostDto, List<MultipartFile> postImageFiles);

    PostDto findById(Long communityId, Long postId, Member currentMember);

    void delete(Long communityId, Long postId, Member currentMember);
}
