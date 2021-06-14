package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.common.MemberProperties;
import hello.sns.repository.MemberRepository;
import hello.sns.service.MemberServiceImpl;
import hello.sns.web.common.RestDocsConfiguration;
import hello.sns.web.dto.member.CreateMemberDto;
import hello.sns.web.dto.member.JwtTokenDto;
import hello.sns.web.dto.member.LoginMemberDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    MemberProperties mProperties;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @DisplayName("회원가입")
    public void createMember() throws Exception {
        // given
        CreateMemberDto requestDto = CreateMemberDto
                .builder()
                .name("Kim")
                .email("jwkim@email.com")
                .password("jwkim1234")
                .build();

        // when
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("create-member",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 페스워드"),
                                fieldWithPath("name").description("회원 이름")
                        ),

                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header")
                        )
                ));
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void getCurrentUser_Success() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/members/me")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(mProperties.getM1Email(), mProperties.getM1Password()))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(mProperties.getM1Email()))
                .andExpect(jsonPath("name").value(mProperties.getM1Name()))
                .andExpect(jsonPath("profileImagePath").exists())
                .andDo(document("get-member",
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("profileImagePath").description("사용자 프로필 이미지 경로")
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
}