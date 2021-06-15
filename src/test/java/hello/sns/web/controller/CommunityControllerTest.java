package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.repository.CategoryRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.MemberRepository;
import hello.sns.service.CategoryService;
import hello.sns.service.CommunityService;
import hello.sns.service.MemberServiceImpl;
import hello.sns.service.PostService;
import hello.sns.web.common.RestDocsConfiguration;
import hello.sns.web.dto.member.CreateMemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class CommunityControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberServiceImpl memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CommunityService communityService;

    @Autowired
    protected CommunityRepository communityRepository;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CategoryRepository categoryRepository;


    private String name = "member0";
    private String email = "member0@email.com";
    private String password = "member1234";

    @BeforeEach
    public void setUp() {
        communityRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();

        CreateMemberDto requestDto = CreateMemberDto
                .builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        memberService.create(requestDto);
    }
}