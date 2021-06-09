package hello.sns.service;

import hello.sns.domain.member.Member;
import hello.sns.web.dto.post.CreateCommentDto;

public interface CommentService {

    Long create(Long communityId, CreateCommentDto createCommentDto, Member currentMember);

    void delete(Long communityId, Long commentId, Member currentMember);

}
