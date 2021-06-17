package com.damoa.web.controller;

import com.damoa.common.PageableValidator;
import com.damoa.domain.member.Member;
import com.damoa.service.AuthService;
import com.damoa.service.CommunityService;
import com.damoa.service.MemberService;
import com.damoa.service.PostService;
import com.damoa.web.dto.common.CurrentMember;
import com.damoa.web.dto.community.JoinedCommunityDto;
import com.damoa.web.dto.member.*;
import com.damoa.web.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.net.URI;
import java.net.URISyntaxException;

@Validated
@RestController
@RequestMapping(value = "/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final PostService postService;
    private final CommunityService communityService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ResponseEntity create(HttpServletRequest httpServletRequest,
                                 @RequestBody @Validated CreateMemberDto createMemberDto) throws URISyntaxException {
        MemberDto memberDto = memberService.create(createMemberDto);
        URI uri = new URI(httpServletRequest.getRequestURL() + "/me");
        return ResponseEntity.created(uri).body(memberDto);
    }

    @GetMapping("/{email}/exists")
    public ResponseEntity checkDuplicatedEmail(@PathVariable @Email String email) {
        memberService.checkDuplicatedEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginMemberDto loginMemberDto) {
        JwtTokenDto jwtToken = authService.login(loginMemberDto);
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@CurrentMember Member currentMember) {
        return ResponseEntity.ok(new MemberDto(currentMember));
    }

    @PostMapping("/profile-image")
    public ResponseEntity updateProfileImage(
            @RequestPart("profileImage") MultipartFile profileImage,
            @CurrentMember Member currentMember) {
        MemberDto memberDto = memberService.updateProfileImage(currentMember, profileImage);
        return ResponseEntity.ok(memberDto);
    }

    @PatchMapping
    public ResponseEntity updateMember(@CurrentMember Member currentMember,
                                       @RequestBody UpdateMemberDto updateMemberDto) {
        MemberDto memberDto = memberService.updateMember(currentMember, updateMemberDto);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/posts")
    public ResponseEntity getMyFeed(@CurrentMember Member currentMember, Pageable pageable) {
        pageableValidator.validate(pageable, 50);
        Page<PostDto> postDtos = postService.findAllByMember(currentMember, pageable);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/communities")
    public ResponseEntity getMyCommunities(@CurrentMember Member currentMember, Pageable pageable) {
        pageableValidator.validate(pageable, 50);
        Page<JoinedCommunityDto> communities = communityService.findByCurrentMember(currentMember, pageable);
        return ResponseEntity.ok(communities);
    }
}
