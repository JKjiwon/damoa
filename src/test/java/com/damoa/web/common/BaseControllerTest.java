package com.damoa.web.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.damoa.repository.*;
import com.damoa.service.*;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, S3MockConfig.class})
@Disabled
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberService memberService;

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

    @Autowired
    protected CommunityMemberRepository communityMemberRepository;

    @Autowired
    protected PostService postService;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected CommentRepository commentRepository;
}
