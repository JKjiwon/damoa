package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import hello.sns.repository.CategoryRepository;
import hello.sns.repository.CommunityRepository;
import hello.sns.repository.MemberRepository;
import hello.sns.service.CategoryService;
import hello.sns.service.CommunityService;
import hello.sns.service.MemberServiceImpl;
import hello.sns.web.common.RestDocsConfiguration;
import hello.sns.web.dto.community.CreateCommunityDto;
import hello.sns.web.dto.community.UpdateCommunityDto;
import hello.sns.web.dto.member.CreateMemberDto;
import hello.sns.web.dto.member.JwtTokenDto;
import hello.sns.web.dto.member.LoginMemberDto;
import hello.sns.web.dto.member.MemberDto;
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

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private Member member1;
    private String member1Email = "member1@email.com";
    private String member1Password = "member1234";

    private Member member2;
    private String member2Email = "member2@email.com";
    private String member2Password = "member1234";

    @BeforeEach
    public void setUp() {
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
    @DisplayName("커뮤니티 생성")
    public void createCommunity_Success() throws Exception {
        // Given
        CreateCommunityDto requestDto = CreateCommunityDto.builder()
                .name("Community1")
                .category("Category")
                .introduction("WelCome to our community")
                .build();

        MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "mainImage.jpg",
                "image/jpg",
                "mainImage".getBytes());

        MockMultipartFile thumbNailImage = new MockMultipartFile(
                "thumbNailImage",
                "thumbNailImage.jpg",
                "image/jpg",
                "thumbNailImage".getBytes());

        // When & Then
        this.mockMvc.perform(
                multipart("/api/communities")
                        .file(mainImage)
                        .file(thumbNailImage)
                        .param("name", requestDto.getName())
                        .param("category", requestDto.getCategory())
                        .param("introduction", requestDto.getIntroduction())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-communities",
                        requestParts(
                                partWithName("thumbNailImage").description("업로드할 커뮤니티 썸네일 이미지 파일"),
                                partWithName("mainImage").description("업로드할 커뮤니티 메인 이미지 파일")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestParameters(
                                parameterWithName("name").description("커뮤니티 이름"),
                                parameterWithName("category").description("커뮤니티 카테고리 이름"),
                                parameterWithName("introduction").description("커뮤니티 소개글")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("커뮤니티 식별자"),
                                fieldWithPath("name").description("커뮤니티 이름"),
                                fieldWithPath("thumbNailImagePath").description("커뮤니티 썸네일 이미지 경로"),
                                fieldWithPath("mainImagePath").description("커뮤니티 메인 이미지 경로"),
                                fieldWithPath("introduction").description("커뮤니티 소개글"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 정보"),
                                fieldWithPath("owner.id").description("커뮤니티 대표 관리자 식별자"),
                                fieldWithPath("owner.name").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("category").description("커뮤니티 카테고리 이름"),
                                fieldWithPath("createdAt").description("커뮤니티 생성 시간"),
                                fieldWithPath("isJoin").description("로그인한 사용자의 커뮤니티 가입 여부")
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 생성_이미지 업로드 실패")
    public void createCommunityWithUploadImageFail_Fail() throws Exception {
        // Given
        CreateCommunityDto requestDto = CreateCommunityDto.builder()
                .name("Community1")
                .category("Category")
                .introduction("WelCome to our community")
                .build();

        MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "mainImage.jpg",
                "audio/mpeg",
                "mainImage".getBytes());

        // When & Then
        this.mockMvc.perform(
                multipart("/api/communities")
                        .file(mainImage)
                        .param("name", requestDto.getName())
                        .param("category", requestDto.getCategory())
                        .param("introduction", requestDto.getIntroduction())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("동일한 커뮤니티가 있으면 커뮤니티 생성 실패")
    public void createCommunity_Fail() throws Exception {
        // Given
        String communityName = "Community1";
        CreateCommunityDto requestDto = CreateCommunityDto.builder()
                .name(communityName)
                .category("Category")
                .introduction("WelCome to our community")
                .build();

        Community community = Community.builder()
                .name(communityName)
                .build();
        communityRepository.save(community);

        // When & Then
        this.mockMvc.perform(
                multipart("/api/communities")
                        .param("name", requestDto.getName())
                        .param("category", requestDto.getCategory())
                        .param("introduction", requestDto.getIntroduction())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    //    /{name}/exists
    @Test
    @DisplayName("커뮤니티 이름 중복 체크")
    public void checkDuplicatedName_Success() throws Exception {
        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{name}/exists", "Communtiy1")
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("check-name",
                        pathParameters(
                                parameterWithName("name").description("중복 체크할 커뮤니티 이름")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 가입")
    public void joinCommunity_Success() throws Exception {
        // given
        Long communityId = createCommunity(1, member1);
        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{id}/join", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("join-communities",
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        )
                ));
    }

    @Test
    @DisplayName("이미 가입한 회원이 커뮤니티에 가입하면 가입 실패")
    public void joinCommunityWithAlreadyJoinedMember_Fail() throws Exception {
        // given
        Long communityId = createCommunity(1, member1);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{id}/join", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("커뮤니티 탈퇴")
    public void withdrawCommunity_Success() throws Exception {
        // given
        Long communityId = createCommunity(1, member1);
        communityService.join(member2, communityId);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{id}/withdraw", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("withdraw-communities",
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        )
                ));
    }

    @Test
    @DisplayName("커뮤니티 대표 관리자가 커뮤니티를 탈퇴할 경우 실패")
    public void withdrawCommunityWithOwner_Fail() throws Exception {
        // given
        Long communityId = createCommunity(1, member1);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{id}/withdraw", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("커뮤니티에 가입하지 않은 회원이 커뮤니티를 탈퇴할 경우 실패")
    public void withdrawCommunityWithNotJoinedMember_Fail() throws Exception {
        // given
        Long communityId = createCommunity(1, member1);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{id}/withdraw", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("커뮤니티 수정")
    public void updateCommunity_Success() throws Exception {
        // Given
        Long communityId = createCommunity(1, member1);

        MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "mainImage.jpg",
                "image/jpg",
                "mainImage".getBytes());

        MockMultipartFile thumbNailImage = new MockMultipartFile(
                "thumbNailImage",
                "thumbNailImage.jpg",
                "image/jpg",
                "thumbNailImage".getBytes());


        UpdateCommunityDto requestDto = UpdateCommunityDto.builder()
                .introduction("Welcome to Spring World")
                .category("Spring")
                .build();

        // When & Then
        this.mockMvc.perform(
                multipart("/api/communities/edit/{id}", communityId)
                        .file(mainImage)
                        .file(thumbNailImage)
                        .param("category", requestDto.getCategory())
                        .param("introduction", requestDto.getIntroduction())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-communities",
                        requestParts(
                                partWithName("thumbNailImage").description("업로드할 커뮤니티 썸네일 이미지 파일"),
                                partWithName("mainImage").description("업로드할 커뮤니티 메인 이미지 파일")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestParameters(
                                parameterWithName("category").description("커뮤니티 카테고리 이름"),
                                parameterWithName("introduction").description("커뮤니티 소개글")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("커뮤니티 식별자"),
                                fieldWithPath("name").description("커뮤니티 이름"),
                                fieldWithPath("thumbNailImagePath").description("커뮤니티 썸네일 이미지 경로"),
                                fieldWithPath("mainImagePath").description("커뮤니티 메인 이미지 경로"),
                                fieldWithPath("introduction").description("커뮤니티 소개글"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 정보"),
                                fieldWithPath("owner.id").description("커뮤니티 대표 관리자 식별자"),
                                fieldWithPath("owner.name").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("category").description("커뮤니티 카테고리 이름"),
                                fieldWithPath("createdAt").description("커뮤니티 생성 시간"),
                                fieldWithPath("isJoin").description("로그인한 사용자의 커뮤니티 가입 여부")
                        )
                ));
    }

    @Test
    @DisplayName("일반 유저가 커뮤니티 수정할 경우 실패")
    public void updateCommunityWithUser_Success() throws Exception {
        // Given
        Long communityId = createCommunity(1, member1);

        UpdateCommunityDto requestDto = UpdateCommunityDto.builder()
                .introduction("Welcome to Spring World")
                .category("Spring")
                .build();

        communityService.join(member2, communityId);

        // When & Then
        this.mockMvc.perform(
                multipart("/api/communities/edit/{id}", communityId)
                        .param("category", requestDto.getCategory())
                        .param("introduction", requestDto.getIntroduction())
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("커뮤니티 조회")
    public void findById_Success() throws Exception {
        // Given
        Long communityId = createCommunity(1, member1);

        // When & Then
        this.mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{id}", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-communities",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        pathParameters(
                                parameterWithName("id").description("커뮤니티 식별자")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("커뮤니티 식별자"),
                                fieldWithPath("name").description("커뮤니티 이름"),
                                fieldWithPath("thumbNailImagePath").description("커뮤니티 썸네일 이미지 경로"),
                                fieldWithPath("mainImagePath").description("커뮤니티 메인 이미지 경로"),
                                fieldWithPath("introduction").description("커뮤니티 소개글"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 정보"),
                                fieldWithPath("owner.id").description("커뮤니티 대표 관리자 식별자"),
                                fieldWithPath("owner.name").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("category").description("커뮤니티 카테고리 이름"),
                                fieldWithPath("createdAt").description("커뮤니티 생성 시간"),
                                fieldWithPath("isJoin").description("로그인한 사용자의 커뮤니티 가입 여부")
                        )
                ));
    }
//    @Test
//    @DisplayName("모든 커뮤니티 조회")
//    public void findAll_Success() throws Exception {
//        // Given
//        Long communityId = createCommunity(1, member1);
//
//        // When & Then
//        this.mockMvc.perform(get("/api/communities")
//                .param("page", "0")
//                .param("size", "10")
//                .param("sort", ""))
//
//                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(document("get-communities",
//                        requestHeaders(
//                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
//                        ),
//                        pathParameters(
//                                parameterWithName("id").description("커뮤니티 식별자")
//                        ),
//                        responseHeaders(
//                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
//                        ),
//                        responseFields(
//                                fieldWithPath("id").description("커뮤니티 식별자"),
//                                fieldWithPath("name").description("커뮤니티 이름"),
//                                fieldWithPath("thumbNailImagePath").description("커뮤니티 썸네일 이미지 경로"),
//                                fieldWithPath("mainImagePath").description("커뮤니티 메인 이미지 경로"),
//                                fieldWithPath("introduction").description("커뮤니티 소개글"),
//                                fieldWithPath("owner").description("커뮤니티 대표 관리자 정보"),
//                                fieldWithPath("owner.id").description("커뮤니티 대표 관리자 식별자"),
//                                fieldWithPath("owner.name").description("커뮤니티 대표 관리자 이름"),
//                                fieldWithPath("category").description("커뮤니티 카테고리 이름"),
//                                fieldWithPath("createdAt").description("커뮤니티 생성 시간"),
//                                fieldWithPath("isJoin").description("로그인한 사용자의 커뮤니티 가입 여부")
//                        )
//                ));
//    }

    private Long createCommunity(int seq, Member member) {
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("Community"+seq)
                .category("Category")
                .introduction("WelCome to our community")
                .build();

        MockMultipartFile mainImage = new MockMultipartFile(
                "mainImage",
                "mainImage.jpg",
                "image/jpg",
                "mainImage".getBytes());

        MockMultipartFile thumbNailImage = new MockMultipartFile(
                "thumbNailImage",
                "thumbNailImage.jpg",
                "image/jpg",
                "thumbNailImage".getBytes());

        return communityService.create(member, createCommunityDto, mainImage, thumbNailImage).getId();
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