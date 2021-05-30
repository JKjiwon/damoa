package hello.sns.web.controller;

import hello.sns.common.BaseControllerTest;
import hello.sns.web.dto.auth.JoinRequest;
import hello.sns.web.dto.auth.JwtAuthenticationResponse;
import hello.sns.web.dto.auth.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberControllerTest extends BaseControllerTest {

    @BeforeEach
    public void setUp() {
        this.memberRepository.deleteAll();
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void getCurrentUser() throws Exception {
        // Given
        String name = "user";
        String email = "user@gmail.com";
        String password = "user1234";

        joinMember(name, email, password);

        // When & Then
        this.mockMvc.perform(get("/api/members/me")
                .header(HttpHeaders.AUTHORIZATION, getAccessToken(email, password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("name").value(name));
    }

    private void joinMember(String name, String email, String password) {
        JoinRequest joinRequest = JoinRequest.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
        authService.join(joinRequest);
    }

    private String getAccessToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);

        ResultActions perform = this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        String responseBody = perform.andReturn().getResponse().getContentAsString();

        JwtAuthenticationResponse response = objectMapper.readValue(responseBody, JwtAuthenticationResponse.class);
        return response.getTokenType() + " " + response.getAccessToken();
    }
}