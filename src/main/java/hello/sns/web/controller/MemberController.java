package hello.sns.web.controller;

import hello.sns.entity.member.Member;
import hello.sns.service.AuthService;
import hello.sns.service.MemberService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.member.*;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity join(@RequestBody @Validated JoinMemberDto joinMemberDto) {
        MemberDto memberDto = memberService.join(joinMemberDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(MemberController.class);
        EntityModel<MemberDto> entityModel = EntityModel.of(
                memberDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(methodOn(MemberController.class).login(null)).withRel("login")
        );
        return ResponseEntity.created(selfLinkBuilder.slash("me").toUri()).body(entityModel); // 나중에 created 로 변경
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginMemberDto loginMemberDto) {
        String jwtToken = authService.login(loginMemberDto);

        EntityModel<JwtTokenDto> entityModel = EntityModel.of(
                new JwtTokenDto(jwtToken),
                linkTo(methodOn(MemberController.class).login(null)).withSelfRel()
        );
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/me")
    @Secured("ROLE_USER")
    public ResponseEntity getCurrentUser(@CurrentMember Member currentMember) {
        EntityModel<MemberDto> entityModel = EntityModel.of(
                new MemberDto(currentMember),
                linkTo(methodOn(MemberController.class).getCurrentUser(null)).withSelfRel(),
                linkTo(MemberController.class).withRel("update"),
                linkTo(methodOn(MemberController.class).updateProfileImage(null, null)).withRel("updateProfile")
        );
        return ResponseEntity.ok(entityModel);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity updateProfileImage(
            @RequestPart("profileImage") MultipartFile profileImage,
            @CurrentMember Member currentMember) {
        MemberDto memberDto = memberService.updateProfileImage(currentMember, profileImage);

        EntityModel<MemberDto> entityModel = EntityModel.of(memberDto,
                linkTo(methodOn(MemberController.class).updateProfileImage(null, null)).withSelfRel(),
                linkTo(methodOn(MemberController.class).getCurrentUser(null)).withRel("query"),
                linkTo(MemberController.class).withRel("update")
        );
        return ResponseEntity.ok(entityModel);
    }

    @PatchMapping
    public ResponseEntity updateMember(@CurrentMember Member currentMember,
                                                  @RequestBody UpdateMemberDto updateMemberDto) {

        MemberDto memberDto = memberService.updateMember(currentMember, updateMemberDto);
        EntityModel<MemberDto> entityModel = EntityModel.of(memberDto,
                linkTo(methodOn(MemberController.class).updateProfileImage(null, null)).withSelfRel(),
                linkTo(methodOn(MemberController.class).getCurrentUser(null)).withRel("query"),
                linkTo(MemberController.class).withRel("update")
        );

        return ResponseEntity.ok(entityModel);
    }
}
