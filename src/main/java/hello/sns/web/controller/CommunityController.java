package hello.sns.web.controller;

import hello.sns.entity.member.Member;
import hello.sns.service.CommunityServiceImpl;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityServiceImpl communityService;

    @PostMapping
    public ResponseEntity createCommunity(
            @Validated CreateCommunityDto createCommunityDto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbNailImage,
            @CurrentMember Member currentMember) throws URISyntaxException {

        CommunityDto communityDto = communityService.create(currentMember, createCommunityDto,
                mainImage, thumbNailImage);

        URI uri = new URI("/api/communities/" + communityDto.getCommunityId());
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
}