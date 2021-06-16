package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.domain.member.Member;
import hello.sns.repository.CategoryRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.MemberRepository;
import hello.sns.repository.PostRepository;
import hello.sns.service.CategoryService;
import hello.sns.service.CommunityService;
import hello.sns.service.MemberServiceImpl;
import hello.sns.service.PostService;
import hello.sns.web.common.RestDocsConfiguration;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.member.JwtTokenDto;
import hello.sns.web.dto.member.LoginMemberDto;
import hello.sns.web.dto.post.CreatePostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class PostControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberServiceImpl memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected CommunityRepository communityRepository;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CommunityService communityService;

    @Autowired
    protected PostService postService;

    @Autowired
    protected PostRepository postRepository;

    private Member member1;
    private String member1Email = "member1@email.com";
    private String member1Password = "member1234";

    private Member member2;
    private String member2Email = "member2@email.com";
    private String member2Password = "member1234";

    private Long community1;
    private Long community2;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        communityRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();

        member1 = createMember(member1Email, member1Password, "memeber1");
        member2 = createMember(member2Email, member2Password, "memeber2");

        community1 = createCommunity(1, member1);
        community2 = createCommunity(2, member2);
    }


    @Test
    @DisplayName("게시글 생성")
    public void createPost() throws Exception {
        // Given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpg",
                "image".getBytes());

        CreatePostDto dto = new CreatePostDto("Post1");

        // When & Then
        this.mockMvc.perform(
                RestDocumentationRequestBuilders
                        .fileUpload("/api/communities/{communityId}/posts", community1)
                        .file(image)
                        .file(image)
                        .param("content", dto.getContent())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-posts",
                        requestParts(
                                partWithName("image").description("업로드 할 이미지, 복수 등록 가능")
                        ),
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestParameters(
                                parameterWithName("content").description("게시글 내용")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("게시글 식별자"),
                                fieldWithPath("content").description("게시글 내용"),
                                fieldWithPath("community").description("커뮤니티 정보"),
                                fieldWithPath("community.id").description("커뮤니티 식별자"),
                                fieldWithPath("community.name").description("커뮤니티 이름"),
                                fieldWithPath("writer").description("작성자 정보"),
                                fieldWithPath("writer.id").description("작성자 식별자"),
                                fieldWithPath("writer.name").description("작성자 이름"),
                                fieldWithPath("images").description("게시글 이미지 정보"),
                                fieldWithPath("images[].id").description("이미지 식별자"),
                                fieldWithPath("images[].path").description("이미지 경로"),
                                fieldWithPath("images[].seq").description("이미지 순서"),
                                fieldWithPath("createdAt").description("게시글 작성 시간"),
                                fieldWithPath("modifiedAt").description("게시글 수정 시간")
                        )
                ));

    }


    public Member createMember(String email, String password, String name) {
        Member member = Member.builder()
                .email(email)
                .password(new BCryptPasswordEncoder().encode(password))
                .name(name)
                .build();
        return memberRepository.save(member);
    }

    private Long createCommunity(int seq, Member member) {
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("Community" + seq)
                .category("Category")
                .introduction("WelCome to our community")
                .build();

        return communityService.create(member, createCommunityDto, null, null).getId();
    }

    private String getAccessToken(String email, String password) throws Exception {
        LoginMemberDto loginMemberDto = new LoginMemberDto(email, password);

        ResultActions perform = this.mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginMemberDto)));

        String responseBody = perform.andReturn().getResponse().getContentAsString();

        JwtTokenDto response = objectMapper.readValue(responseBody, JwtTokenDto.class);
        return response.getTokenType() + " " + response.getAccessToken();
    }

}