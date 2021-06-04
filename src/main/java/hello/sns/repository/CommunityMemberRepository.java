package hello.sns.repository;

import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    Boolean existsByMember(Member member);
}
