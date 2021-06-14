//package hello.sns.web.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import hello.sns.security.JwtAuthenticationEntryPoint;
//import hello.sns.service.MemberServiceImpl;
//import hello.sns.web.dto.member.CreateMemberDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.restdocs.headers.HeaderDocumentation.*;
//import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
//import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class MemberControllerTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MockMvc mvc;
//    @MockBean
//    private MemberServiceImpl memberService;
//
//    @MockBean
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    @Test
//    @DisplayName("회원가입")
//    public void joinMember() throws Exception {
//        // given
//        CreateMemberDto requestDto = CreateMemberDto
//                .builder()
//                .name("Kim JiWon")
//                .email("jwkim@gmail.com")
//                .password("member1234")
//                .build();
//
//        // when
//        mvc.perform(post("/api/members")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(requestDto)))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(header().exists(HttpHeaders.LOCATION));
//        // then
//    }
//
//}
