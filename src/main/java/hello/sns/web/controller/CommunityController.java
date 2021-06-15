package hello.sns.web.controller;

import hello.sns.common.PageableValidator;
import hello.sns.domain.member.Member;
import hello.sns.service.CommunityServiceImpl;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CommunityMemberDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityServiceImpl communityService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ResponseEntity create(
            HttpServletRequest httpServletRequest,
            @Validated CreateCommunityDto createCommunityDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage,
            @CurrentMember Member currentMember) throws URISyntaxException {

        CommunityDto communityDto = communityService.create(currentMember, createCommunityDto,
                mainImage, thumbNailImage);

        URI uri = new URI(httpServletRequest.getRequestURL().toString() + communityDto.getId());
        return ResponseEntity.created(uri).body(communityDto);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity join(
            @PathVariable("id") Long communityId,
            @CurrentMember Member currentMember) {
        communityService.join(currentMember, communityId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity withdraw(
            @PathVariable("id") Long communityId,
            @CurrentMember Member currentMember) {

        communityService.withdraw(currentMember, communityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity update(
            @PathVariable("id") Long communityId,
            @CurrentMember Member currentMember,
            @Validated UpdateCommunityDto updateCommunityDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage) {

        CommunityDto communityDto = communityService
                .update(communityId, currentMember, updateCommunityDto, mainImage, thumbNailImage);
        return ResponseEntity.ok().body(communityDto);
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity checkDuplicatedName(@PathVariable String name) {
        communityService.checkDuplicatedName(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(
            @PathVariable("id") Long communityId,
            @CurrentMember Member currentMember) {
        CommunityDto communityDto = communityService.findById(communityId, currentMember);
        return ResponseEntity.ok(communityDto);
    }

    // 커뮤니티 검색 기능 -> QueryDSL -> 커뮤니티명 or 소개글 or 카테고리명 로 검색
    @GetMapping
    public ResponseEntity findAllSearch(
            @CurrentMember Member currentMember,
            Pageable pageable,
            String search) {

        pageableValidator.validate(pageable, 100);
        Page<CommunityDto> communityDtos = communityService.findAllSearch(currentMember, pageable, search);
        return ResponseEntity.ok(communityDtos);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity findMember(
            @CurrentMember Member currentMember,
            @PathVariable("id") Long communityId,
            Pageable pageable) {

        pageableValidator.validate(pageable, 100);
        Page<CommunityMemberDto> communityMemberDtos = communityService.findCommunityMember(communityId, currentMember, pageable);
        return ResponseEntity.ok(communityMemberDtos);
    }
}