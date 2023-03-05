package com.damoa.web.controller;

import com.damoa.common.PageableValidator;
import com.damoa.domain.member.entity.Member;
import com.damoa.domain.community.service.CommunityServiceImpl;
import com.damoa.web.dto.common.CurrentMember;
import com.damoa.domain.community.dto.CommunityDto;
import com.damoa.domain.community.dto.CommunityMemberDto;
import com.damoa.domain.community.dto.CreateCommunityDto;
import com.damoa.domain.community.dto.UpdateCommunityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
            @Valid CreateCommunityDto dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage,
            @CurrentMember Member currentMember) throws URISyntaxException {

        CommunityDto communityDto = communityService.create(currentMember, dto,
                mainImage, thumbNailImage);
        URI uri = new URI(httpServletRequest.getRequestURL().toString() + communityDto.getId());
        return ResponseEntity.created(uri).body(communityDto);
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity join(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember) {

        communityService.join(currentMember, communityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{communityId}/withdraw")
    public ResponseEntity withdraw(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember) {

        communityService.withdraw(currentMember, communityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{communityId}/edit")
    public ResponseEntity update(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember,
            @Valid UpdateCommunityDto dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage) {

        CommunityDto communityDto = communityService
                .update(communityId, currentMember, dto, mainImage, thumbNailImage);
        return ResponseEntity.ok().body(communityDto);
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity checkDuplicatedName(@PathVariable String name) {
        communityService.checkDuplicatedName(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{communityId}")
    public ResponseEntity findById(
            @PathVariable("communityId") Long communityId,
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

    @GetMapping("/{communityId}/members")
    public ResponseEntity findMember(
            @CurrentMember Member currentMember,
            @PathVariable("communityId") Long communityId,
            Pageable pageable) {

        pageableValidator.validate(pageable, 100);
        Page<CommunityMemberDto> communityMemberDtos = communityService.findCommunityMember(communityId, currentMember, pageable);
        return ResponseEntity.ok(communityMemberDtos);
    }
}