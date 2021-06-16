package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.domain.member.Member;
import hello.sns.repository.CategoryRepository;
import hello.sns.repository.CommunityMemberRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.MemberRepository;
import hello.sns.service.CategoryService;
import hello.sns.service.CommunityService;
import hello.sns.service.MemberServiceImpl;
import hello.sns.service.PostService;
import hello.sns.web.common.RestDocsConfiguration;
import hello.sns.web.dto.community.CommunityDto;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.member.*;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class MemberControllerTest {

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
    protected CommunityMemberRepository communityMemberRepository;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CommunityService communityService;

    @Autowired
    protected PostService postService;

    private Member member1;
    private String member1Email = "member1@email.com";
    private String member1Password = "member1234";

    private Member member2;
    private String member2Email = "member2@email.com";
    private String member2Password = "member1234";

    @BeforeEach
    public void setUp() {
        communityMemberRepository.deleteAll();
        communityRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();

        CreateMemberDto requestDto = CreateMemberDto
                .builder()
                .name("member1")
                .email(member1Email)
                .password(member1Password)
                .build();
        MemberDto dto1 = memberService.create(requestDto);
        member1 = memberRepository.findById(dto1.getId()).get();


        CreateMemberDto requestDto2 = CreateMemberDto
                .builder()
                .name("member2")
                .email(member2Email)
                .password(member2Password)
                .build();
        MemberDto dto2 = memberService.create(requestDto2);
        member2 = memberRepository.findById(dto2.getId()).get();
    }

    @Test
    @DisplayName("회원가입")
    public void createMember_Success() throws Exception {
        // given
        CreateMemberDto requestDto = CreateMemberDto
                .builder()
                .name("Kim")
                .email("jwkim@email.com")
                .password("jwkim1234")
                .build();

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("create-members",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestFields(
                                fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 페스워드"),
                                fieldWithPath("name").description("회원 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입"),
                                headerWithName(HttpHeaders.LOCATION).description("Location")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("profileImagePath").description("사용자 프로필 이미지 경로"),
                                fieldWithPath("joinedAt").description("사용자 가입 시기")
                        )
                ));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입_실패")
    public void createMember_duplicatedEmail_Fail() throws Exception {
        // given
        CreateMemberDto requestDto = CreateMemberDto
                .builder()
                .name("member3")
                .email(member1Email)
                .password("member1234")
                .build();

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 중복 체크")
    public void checkDuplicatedEmail_Success() throws Exception {
        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/members/{email}/exists", "member3@email.com"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("check-email",
                        pathParameters(
                                parameterWithName("email").description("중복 체크할 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("중복된 이메일로 중복체크_실패")
    public void checkDuplicatedEmail_Fail() throws Exception {
        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/members/{email}/exists", member1Email))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인")
    public void login_Success() throws Exception {
        // given
        LoginMemberDto loginMemberDto = new LoginMemberDto(member1Email, member1Password);
        // when
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginMemberDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("login-members",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 페스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("tokenType").description("토큰 타입"),
                                fieldWithPath("accessToken").description("인증 토큰"),
                                fieldWithPath("expiryDate").description("만료 시기")
                        )
                ));
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void getCurrentUser_Success() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/members/me")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-members",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("profileImagePath").description("사용자 프로필 이미지 경로"),
                                fieldWithPath("joinedAt").description("사용자 가입 시기")
                        )
                ));
    }

    @Test
    @DisplayName("현재 사용자 정보 업데이트")
    void updateCurrentUser_Success() throws Exception {
        // Given
        UpdateMemberDto requestDto = UpdateMemberDto.builder().name("로프트").build();
        // When & Then
        this.mockMvc.perform(patch("/api/members")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("update-members",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestFields(
                                fieldWithPath("name").description("변경할 사용자 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("profileImagePath").description("사용자 프로필 이미지 경로"),
                                fieldWithPath("joinedAt").description("사용자 가입 시기")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 이미지 업데이트")
    void updateProfile_Success() throws Exception {
        // Given
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "ProfileImageFile.jpg",
                "image/jpg",
                "ProfileImageFile".getBytes());
        // When & Then
        this.mockMvc.perform(
                multipart("/api/members/profile-image")
                        .file(profileImage)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("change-profile-image",
                        requestParts(
                                partWithName("profileImage").description("업로드할 이미지 파일")),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("profileImagePath").description("사용자 프로필 이미지 경로"),
                                fieldWithPath("joinedAt").description("사용자 가입 시기")
                        )
                ));
    }

    @Test
    @DisplayName("현재 사용자가 가입한 모든 커뮤니티 조회")
    void queryCommunitiesOfMember_Success() throws Exception {
        // Given
        // Member1 이 만든 커뮤니티
        IntStream.rangeClosed(1, 3).forEach(
                i -> createCommunity(i, member1));

        // Member2 가 가입한 커뮤니티
        List<Long> communityIds = new ArrayList<>();
        IntStream.rangeClosed(4, 6).forEach(
                i -> communityIds.add(createCommunity(i, member2)));

        communityIds.forEach(
                i -> communityService.join(member1, i)
        );

        // When & Then
        this.mockMvc.perform(get("/api/members/communities")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password))
                .param("page", "0")
                .param("size", "10")
                .param("sort", "joinedAt,desc"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document(
                        "query-joined-communities",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        requestParameters(
                                parameterWithName("page").description("요청할 페이지 번호"),
                                parameterWithName("size").description("한 페이지 당 개수"),
                                parameterWithName("sort").description("정렬 기준 필드, asc or desc")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                // 커뮤니티 정보
                                fieldWithPath("content").description("받아온 커뮤니티 정보"),
                                fieldWithPath("content[].id").description("커뮤니티 아이디"),
                                fieldWithPath("content[].name").description("커뮤니티 이름"),
                                fieldWithPath("content[].thumbNailImagePath").description("커뮤니티 섬네일 이미지 경로"),
                                fieldWithPath("content[].introduction").description("커뮤니티 소개"),
                                fieldWithPath("content[].owner").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("content[].category").description("커뮤니티 카테고리"),
                                fieldWithPath("content[].memberCount").description("커뮤니티 회원수"),
                                fieldWithPath("content[].grade").description("사용자의 커뮤니티 등급"),
                                fieldWithPath("content[].joinedAt").description("커뮤니티 가입 날짜"),
                                // 페이징 정보
                                fieldWithPath("pageable").description("페이징 관련 정보"),
                                fieldWithPath("pageable.sort").description("페이지 내 정렬 관련 정보"),
                                fieldWithPath("pageable.sort.sorted").description("페이징 정렬 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("페이징 정렬이 되지않았는지 여부"),
                                fieldWithPath("pageable.sort.empty").description("페이징이 비어있는지 여부"),
                                fieldWithPath("pageable.offset").description("페이징 Offset 정보"),
                                fieldWithPath("pageable.pageSize").description("페이징 사이즈 정보"),
                                fieldWithPath("pageable.pageNumber").description("페이징 번호(0부터 시작)"),
                                fieldWithPath("pageable.paged").description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").description("페이징 되지않았는지 여부"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("last").description("마지막 페이지인지 확인"),
                                fieldWithPath("totalElements").description("전체 페이지의 총 원소의 수"),
                                fieldWithPath("number").description("페이지 넘버"),
                                fieldWithPath("size").description("페이지 나누는 사이즈"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 원소의 수"),
                                fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                fieldWithPath("sort").description("정렬 관련 정보"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("정렬이 되지않았는지 여부"),
                                fieldWithPath("sort.empty").description("정렬값이 비어있는지 여부"),
                                fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                fieldWithPath("empty").description("정보가 비어있는지 여부")
                        )
                ));
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

    private Long createCommunity(int seq, Member member) {

        MockMultipartFile ImageFile = new MockMultipartFile(
                "Image",
                "ImageFile.jpg",
                "image/jpg",
                "ProfileImageFile".getBytes());

        CreateCommunityDto dto = CreateCommunityDto.builder()
                .name("Community" + seq)
                .category("category")
                .introduction("Hello World")
                .build();
        CommunityDto communityDto = communityService.create(member, dto, ImageFile, ImageFile);
        return communityDto.getId();
    }
}