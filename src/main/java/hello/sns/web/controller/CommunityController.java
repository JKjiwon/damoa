package hello.sns.web.controller;

import hello.sns.domain.member.Member;
import hello.sns.service.CommunityServiceImpl;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.community.CommunityDto;
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

    @PostMapping
    public ResponseEntity createCommunity(
            HttpServletRequest httpServletRequest,
            @Validated CreateCommunityDto createCommunityDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage,
            @CurrentMember Member currentMember) throws URISyntaxException {

        CommunityDto communityDto = communityService.create(currentMember, createCommunityDto,
                mainImage, thumbNailImage);

        URI uri = new URI(httpServletRequest.getRequestURI() + "/" + communityDto.getCommunityId());
        return ResponseEntity.created(uri).body(communityDto);
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity joinMember(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember) {
        communityService.join(currentMember, communityId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityId}/withdraw")
    public ResponseEntity withdrawMember(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember) {

        communityService.withdraw(currentMember, communityId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{communityId}")
    public ResponseEntity updateCommunity(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember,
            @Validated UpdateCommunityDto updateCommunityDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage) {

        CommunityDto communityDto =
                communityService.update(communityId, currentMember, updateCommunityDto, mainImage, thumbNailImage);
        return ResponseEntity.ok(communityDto);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity findById(
            @PathVariable("communityId") Long communityId,
            @CurrentMember Member currentMember) {
        CommunityDto communityDto = communityService.findById(communityId, currentMember);
        return ResponseEntity.ok(communityDto);
    }

    @GetMapping
    public ResponseEntity findAll(
            @CurrentMember Member currentMember,
            Pageable pageable) {
        Page<CommunityDto> communityDtos = communityService.findByAll(currentMember, pageable);
        return ResponseEntity.ok(communityDtos);
    }

    @GetMapping("/{name}/exists")
    public ResponseEntity checkDuplicatedName(@PathVariable String name) {
        communityService.checkDuplicatedName(name);
        return ResponseEntity.ok().build();
    }
    // 커뮤니티 검색 기능 - QueryDSL ?? -> 커뮤니티명 + 소개글 + 카테고리명??
}
