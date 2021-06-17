package com.damoa.web.controller;

import com.damoa.domain.community.Community;
import com.damoa.domain.member.Member;
import com.damoa.web.common.BaseControllerTest;
import com.damoa.web.dto.community.CreateCommunityDto;
import com.damoa.web.dto.community.UpdateCommunityDto;
import com.damoa.web.dto.member.CreateMemberDto;
import com.damoa.web.dto.member.JwtTokenDto;
import com.damoa.web.dto.member.LoginMemberDto;
import com.damoa.web.dto.member.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommunityControllerTest extends BaseControllerTest {

    private Member member1;
    private String member1Email = "member1@email.com";
    private String member1Password = "member1234";

    private Member member2;
    private String member2Email = "member2@email.com";
    private String member2Password = "member1234";

    private Member member3;
    private String member3Email = "member3@email.com";
    private String member3Password = "member1234";

    @BeforeEach
    public void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
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

        CreateMemberDto requestDto3 = CreateMemberDto
                .builder()
                .name("member3")
                .email(member3Email)
                .password(member3Password)
                .build();
        MemberDto dto3 = memberService.create(requestDto3);
        member3 = memberRepository.findById(dto3.getId()).get();
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
                                fieldWithPath("memberCount").description("커뮤니티 회원수"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 이름"),
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
                        .post("/api/communities/{communityId}/join", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("join-communities",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
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
                        .post("/api/communities/{communityId}/join", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isForbidden());
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
                        .post("/api/communities/{communityId}/withdraw", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("withdraw-communities",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
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
                        .post("/api/communities/{communityId}/withdraw", communityId)
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
                        .post("/api/communities/{communityId}/withdraw", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isForbidden());
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
                RestDocumentationRequestBuilders
                        .fileUpload("/api/communities/{communityId}/edit", communityId)
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
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
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
                                fieldWithPath("memberCount").description("커뮤니티 회원수"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 이름"),
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
                multipart("/api/communities/{communityId}/edit", communityId)
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
                        .get("/api/communities/{communityId}", communityId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-communities",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
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
                                fieldWithPath("memberCount").description("커뮤니티 회원수"),
                                fieldWithPath("owner").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("category").description("커뮤니티 카테고리 이름"),
                                fieldWithPath("createdAt").description("커뮤니티 생성 시간"),
                                fieldWithPath("isJoin").description("로그인한 사용자의 커뮤니티 가입 여부")
                        )
                ));
    }

    @Test
    @DisplayName("모든 커뮤니티 조회")
    public void findAll_Success() throws Exception {
        // Given
        // 회원이 1명인 커뮤니티
        createCommunity(1, member2);
        // 회원이 2명인 커뮤니티
        IntStream.rangeClosed(2, 3).forEach(
                i -> {
                    Long communityId = createCommunity(i, member2);
                    communityService.join(member3, communityId);
                }
        );
        // 회원이 3명인 커뮤니티
        IntStream.rangeClosed(4, 5).forEach(
                i -> {
                    Long communityId = createCommunity(i, member1);
                    communityService.join(member2, communityId);
                    communityService.join(member3, communityId);
                }
        );

        // When & Then
        this.mockMvc.perform(get("/api/communities")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "memberCount,desc")
                .param("search", "com")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "query-communities",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        requestParameters(
                                parameterWithName("page").description("요청할 페이지 번호"),
                                parameterWithName("size").description("한 페이지 당 개수"),
                                parameterWithName("sort").description("정렬 기준 필드, asc or desc"),
                                parameterWithName("search").description("검색 키워드(이름+소개글+카테고리)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                // 커뮤니티 정보
                                fieldWithPath("content").description("받아온 커뮤니티 정보"),
                                fieldWithPath("content[].id").description("커뮤니티 식별자"),
                                fieldWithPath("content[].name").description("커뮤니티 이름"),
                                fieldWithPath("content[].thumbNailImagePath").description("커뮤니티 섬네일 이미지 경로"),
                                fieldWithPath("content[].mainImagePath").description("커뮤니티 메인 이미지 경로"),
                                fieldWithPath("content[].introduction").description("커뮤니티 소개"),
                                fieldWithPath("content[].owner").description("커뮤니티 대표 관리자 이름"),
                                fieldWithPath("content[].category").description("커뮤니티 카테고리"),
                                fieldWithPath("content[].memberCount").description("커뮤니티 회원수"),
                                fieldWithPath("content[].createdAt").description("커뮤니티 생성 날짜"),
                                fieldWithPath("content[].isJoin").description("커뮤니티 가입 여부"),
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

    @Test
    @DisplayName("모든 커뮤니티 멤버 조회")
    public void findAllMember_Success() throws Exception {
        // Given

        Long communityId = createCommunity(1, member1);
        communityService.join(member2, communityId);
        communityService.join(member3, communityId);

        // When & Then
        this.mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{communityId}/members", communityId)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "joinedAt,desc")
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                        "query-communities-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
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
                                // 커뮤니티 사용자 조회
                                fieldWithPath("content").description("받아온 커뮤니티 사용자 정보"),
                                fieldWithPath("content[].id").description("사용자 식별자"),
                                fieldWithPath("content[].name").description("사용자 이름"),
                                fieldWithPath("content[].email").description("사용자 이메일"),
                                fieldWithPath("content[].profileImagePath").description("사용자 프로필 이미지 경로"),
                                fieldWithPath("content[].grade").description("사용자 커뮤니티 등급"),
                                fieldWithPath("content[].joinedAt").description("사용자 커뮤니티 가입 날짜"),

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

    private Long createCommunity(int seq, Member member) {
        CreateCommunityDto createCommunityDto = CreateCommunityDto.builder()
                .name("Community" + seq)
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