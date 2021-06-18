package com.damoa.service;

import com.damoa.domain.member.Member;
import com.damoa.web.dto.post.CreatePostDto;
import com.damoa.web.dto.post.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostDto create(Long communityId, Member currentMember,
                   CreatePostDto dto, List<MultipartFile> postImageFiles);

    void delete(Long communityId, Long postId, Member currentMember);

    PostDto findById(Long communityId, Long postId, Member currentMember);

    Page<PostDto> findAllByCommunityId(Long communityId, Member currentMember, Pageable pageable);

    Page<PostDto> findAllByMember(Member currentMember, Pageable pageable);
}
