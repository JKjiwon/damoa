package hello.sns.service;

import hello.sns.entity.member.Member;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.post.CreatePostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    void createPost(Long communityId, Member currentMember, CreatePostDto createPostDto, List<MultipartFile> postImageFiles);
}
