package com.damoa.web.controller;

import com.damoa.domain.member.Member;
import com.damoa.web.common.BaseControllerTest;
import com.damoa.web.dto.community.CreateCommunityDto;
import com.damoa.web.dto.member.JwtTokenDto;
import com.damoa.web.dto.member.LoginMemberDto;
import com.damoa.web.dto.post.CreatePostDto;
import com.damoa.web.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends BaseControllerTest {

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
        commentRepository.deleteAll();
        postRepository.deleteAll();
        communityMemberRepository.deleteAll();
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
    public void createPost_Success() throws Exception {
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

    @Test
    @DisplayName("게시글 삭제")
    public void deletePost_Success() throws Exception {
        // given
        Long postId = createPost(community1, member1, 1);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .delete("/api/communities/{communityId}/posts/{postId}", community1, postId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("delete-posts",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void getPost_Success() throws Exception {
        // given
        Long postId = createPost(community1, member1, 1);

        // when
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{communityId}/posts/{postId}", community1, postId)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-posts",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
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


    @Test
    @DisplayName("모든 게시글 조회")
    public void queryPosts_Success() throws Exception {
        // given
        // member1 게시글
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community1, member1, i));

        // member2 게시글
        communityService.join(member2, community1);
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community1, member2, i));

        // when : member2가 community1의 게시글 조회
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{communityId}/posts", community1)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query-posts",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자")
                        ),
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
                                fieldWithPath("content").description("받아온 게시글 정보"),
                                fieldWithPath("content[].id").description("게시글 식별자"),
                                fieldWithPath("content[].content").description("게시글 내용"),
                                fieldWithPath("content[].community").description("커뮤니티 정보"),
                                fieldWithPath("content[].community.id").description("커뮤니티 식별자"),
                                fieldWithPath("content[].community.name").description("커뮤니티 이름"),
                                fieldWithPath("content[].writer").description("작성자 정보"),
                                fieldWithPath("content[].writer.id").description("작성자 식별자"),
                                fieldWithPath("content[].writer.name").description("작성자 이름"),
                                fieldWithPath("content[].images").description("게시글 이미지 정보"),
                                fieldWithPath("content[].images[].id").description("이미지 식별자"),
                                fieldWithPath("content[].images[].path").description("이미지 경로"),
                                fieldWithPath("content[].images[].seq").description("이미지 순서"),
                                fieldWithPath("content[].createdAt").description("게시글 작성 시간"),
                                fieldWithPath("content[].modifiedAt").description("게시글 수정 시간"),
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
    @DisplayName("사용자의 모든 커뮤니티의 모든 게시글 조회, 즉 SNS의 Feed와 같은 역할을 한다.")
    public void queryPostsByMember_Success() throws Exception {
        // given
        // community1의 member1 게시글
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community1, member1, i));

        // community1의 member1 게시글
        communityService.join(member2, community1);
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community1, member2, i));


        // community2의 member1 게시글
        communityService.join(member1, community2);
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community2, member1, i));

        // community2의 member2 게시글
        IntStream.rangeClosed(1, 3).forEach(
                i -> createPost(community2, member2, i));


        // when : member2가 community1의 게시글 조회
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/members/posts")
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member2Email, member2Password))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query-members-posts",
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
                                fieldWithPath("content").description("받아온 게시글 정보"),
                                fieldWithPath("content[].id").description("게시글 식별자"),
                                fieldWithPath("content[].content").description("게시글 내용"),
                                fieldWithPath("content[].community").description("커뮤니티 정보"),
                                fieldWithPath("content[].community.id").description("커뮤니티 식별자"),
                                fieldWithPath("content[].community.name").description("커뮤니티 이름"),
                                fieldWithPath("content[].writer").description("작성자 정보"),
                                fieldWithPath("content[].writer.id").description("작성자 식별자"),
                                fieldWithPath("content[].writer.name").description("작성자 이름"),
                                fieldWithPath("content[].images").description("게시글 이미지 정보"),
                                fieldWithPath("content[].images[].id").description("이미지 식별자"),
                                fieldWithPath("content[].images[].path").description("이미지 경로"),
                                fieldWithPath("content[].images[].seq").description("이미지 순서"),
                                fieldWithPath("content[].createdAt").description("게시글 작성 시간"),
                                fieldWithPath("content[].modifiedAt").description("게시글 수정 시간"),
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

    private Long createPost(Long communityId, Member member, int postSeq) {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpg",
                "image".getBytes());

        CreatePostDto dto = new CreatePostDto("Post" + postSeq);
        PostDto postDto = postService.create(communityId, member, dto, List.of(image, image));
        return postDto.getId();
    }


    private Member createMember(String email, String password, String name) {
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