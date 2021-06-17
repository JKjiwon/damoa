package com.damoa.web.controller;

import com.damoa.domain.member.Member;
import com.damoa.web.common.BaseControllerTest;
import com.damoa.web.dto.community.CreateCommunityDto;
import com.damoa.web.dto.member.JwtTokenDto;
import com.damoa.web.dto.member.LoginMemberDto;
import com.damoa.web.dto.post.CreateCommentDto;
import com.damoa.web.dto.post.CreatePostDto;
import com.damoa.web.dto.post.PostDto;
import com.damoa.web.dto.post.UpdateCommentDto;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentControllerTest extends BaseControllerTest {

    private Member member1;
    private String member1Email = "member1@email.com";
    private String member1Password = "member1234";

    private Member member2;
    private String member2Email = "member2@email.com";
    private String member2Password = "member1234";

    private Long community1;
    private Long post1;

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
        joinCommunity(member2, community1);
        post1 = createPost(community1, member1, 1);
    }

    @Test
    @DisplayName("댓글 생성")
    public void createComment_Success() throws Exception {
        // given
        Long comment1 = createComment(1, null, member2);
        CreateCommentDto requestDto = new CreateCommentDto("Comment2", comment1);

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/communities/{communityId}/posts/{postId}/comments", community1, post1)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-comments",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("parentCommentId").description("부모 댓글 식별자, 부모 댓글이 없으면 content만 전송")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("댓글 식별자"),
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("postId").description("게시글 식별자"),
                                fieldWithPath("writer").description("댓글 작성자 정보"),
                                fieldWithPath("writer.id").description("댓글 작성자 식별자"),
                                fieldWithPath("writer.name").description("댓글 작성자 이름"),
                                fieldWithPath("parentId").description("부모 댓글 식별자, 부모 댓글이 없으면 보이지 않는다."),
                                fieldWithPath("countOfSubComments").description("자식 댓글 개수"),
                                fieldWithPath("createdAt").description("댓글 작성 시간")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제")
    public void deleteComments_Success() throws Exception {
        // given
        Long comment1 = createComment(1, null, member1);
        Long comment2 = createComment(2, comment1, member2);

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .delete("/api/communities/{communityId}/posts/{postId}/comments/{commentId}",
                                community1, post1, comment1)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("delete-comments",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자"),
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 업데이트")
    public void updateComment_Success() throws Exception {
        // given
        Long comment1 = createComment(1, null, member1);
        UpdateCommentDto requestDto = new UpdateCommentDto("Update Comment1");

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .patch("/api/communities/{communityId}/posts/{postId}/comments/{commentId}",
                                community1, post1, comment1)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-comments",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자"),
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        requestFields(
                                fieldWithPath("content").description("변경할 댓글 내용")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("댓글 식별자"),
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("postId").description("게시글 식별자"),
                                fieldWithPath("writer").description("댓글 작성자 정보"),
                                fieldWithPath("writer.id").description("댓글 작성자 식별자"),
                                fieldWithPath("writer.name").description("댓글 작성자 이름"),
                                fieldWithPath("countOfSubComments").description("자식 댓글 개수"),
                                fieldWithPath("createdAt").description("댓글 작성 시간")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 조회")
    public void getComment_Success() throws Exception {
        // given
        Long comment1 = createComment(1, null, member2);
        IntStream.rangeClosed(2, 5)
                .forEach(i -> createComment(i, comment1, member1));

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{communityId}/posts/{postId}/comments/{commentId}"
                                , community1, post1, comment1)
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-comments",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자"),
                                parameterWithName("commentId").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("인증 정보")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("댓글 식별자"),
                                fieldWithPath("content").description("댓글 내용"),
                                fieldWithPath("postId").description("게시글 식별자"),
                                fieldWithPath("writer").description("댓글 작성자 정보"),
                                fieldWithPath("writer.id").description("댓글 작성자 식별자"),
                                fieldWithPath("writer.name").description("댓글 작성자 이름"),
                                fieldWithPath("createdAt").description("댓글 작성 시간"),
                                fieldWithPath("countOfSubComments").description("자식 댓글 개수"),
                                fieldWithPath("subComments").description("자식 댓글 정보"),
                                fieldWithPath("subComments[].id").description("자식 댓글 식별자"),
                                fieldWithPath("subComments[].content").description("자식 댓글 내용"),
                                fieldWithPath("subComments[].writer").description("자식 댓글 작성자 정보"),
                                fieldWithPath("subComments[].writer.id").description("자식 댓글 작성자 식별자"),
                                fieldWithPath("subComments[].writer.name").description("자식 댓글 작성자 이름"),
                                fieldWithPath("subComments[].createdAt").description("자식 댓글 작성 시간")
                        )
                ));
    }

    @Test
    @DisplayName("모든 부모 댓글 조회")
    public void queryParentComments_Success() throws Exception {
        // given
        // 부모 댓글 id:1
        Long comment1 = createComment(1, null, member2);
        IntStream.rangeClosed(2, 5)
                .forEach(i -> createComment(i, comment1, member1));
        // 부모 댓글 id:6
        Long comment2 = createComment(6, null, member1);
        IntStream.rangeClosed(7, 10)
                .forEach(i -> createComment(i, comment1, member2));
        // 부모 댓글 id:11 ~ 20
        IntStream.rangeClosed(11, 20)
                .forEach(i -> createComment(i, null, member2));

        // when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/communities/{communityId}/posts/{postId}/comments"
                                , community1, post1, comment1)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .header(HttpHeaders.AUTHORIZATION, getAccessToken(member1Email, member1Password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query-parent-comments",
                        pathParameters(
                                parameterWithName("communityId").description("커뮤니티 식별자"),
                                parameterWithName("postId").description("게시글 식별자")
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
                                fieldWithPath("content[]").description("받아온 댓글 정보"),
                                fieldWithPath("content[].id").description("댓글 식별자"),
                                fieldWithPath("content[].content").description("댓글 내용"),
                                fieldWithPath("content[].postId").description("게시글 식별자"),
                                fieldWithPath("content[].writer").description("댓글 작성자 정보"),
                                fieldWithPath("content[].writer.id").description("댓글 작성자 식별자"),
                                fieldWithPath("content[].writer.name").description("댓글 작성자 이름"),
                                fieldWithPath("content[].createdAt").description("댓글 작성 시간"),
                                fieldWithPath("content[].countOfSubComments").description("자식 댓글 개수"),
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

    private Long createComment(int seq, Long parentId, Member member) {
        CreateCommentDto dto1 = new CreateCommentDto("Comment" + seq, parentId);
        return commentService.create(community1, post1, dto1, member).getId();
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

    private void joinCommunity(Member member, Long communityId) {
        communityService.join(member, community1);
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
