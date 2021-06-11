package hello.sns.web.controller;

import hello.sns.domain.member.Member;
import hello.sns.service.AuthService;
import hello.sns.service.MemberService;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.member.*;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity join(HttpServletRequest httpServletRequest,
            @RequestBody @Validated CreateMemberDto createMemberDto) throws URISyntaxException {
        memberService.join(createMemberDto);
        URI uri = new URI(httpServletRequest.getRequestURI() + "/me");
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{email}/exists")
    public ResponseEntity checkDuplicatedEmail(@PathVariable @Email String email) {
        memberService.checkDuplicatedEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginMemberDto loginMemberDto) {
        String jwtToken = authService.login(loginMemberDto);
        return ResponseEntity.ok(new JwtTokenDto(jwtToken));
    }

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@CurrentMember Member currentMember) {
        return ResponseEntity.ok(new MemberDto(currentMember));
    }

    @PutMapping("/profile-image")
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
}
