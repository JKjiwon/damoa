package hello.sns.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.sns.repository.MemberRepository;
import hello.sns.service.AuthService;
import hello.sns.web.dto.request.JoinRequest;
import hello.sns.web.dto.request.LoginRequest;
import hello.sns.web.dto.response.JwtAuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        this.memberRepository.deleteAll();
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void getCurrentUser() throws Exception {
        // Given
        JoinRequest joinRequest = JoinRequest.builder()
                .name("user")
                .email("user@email.com")
                .password("user1234")
                .build();

        // When & Then
        this.mockMvc.perform(get("/api/members/me")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(joinRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(joinRequest.getEmail()))
                .andExpect(jsonPath("name").value(joinRequest.getName()));
    }

    private String getAccessToken(JoinRequest joinRequest) throws Exception {

        authService.join(joinRequest);
        LoginRequest loginRequest = new LoginRequest(joinRequest.getEmail(), joinRequest.getPassword());

        ResultActions perform = this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        String responseBody = perform.andReturn().getResponse().getContentAsString();

        JwtAuthenticationResponse response = objectMapper.readValue(responseBody, JwtAuthenticationResponse.class);
        return response.getTokenType() + " " + response.getAccessToken();
    }
}