package hello.sns.repository;

import hello.sns.domain.community.Community;
import hello.sns.domain.community.CommunityMember;
import hello.sns.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    boolean existsByMemberAndCommunityId(Member member, Long communityId);

    @EntityGraph(attributePaths = {"community", "member"})
    Optional<CommunityMember> findByMemberAndCommunityId(
            Member member, Long communityId);

    Optional<CommunityMember> findByMemberIdAndCommunityId(
            Long communityId, Long memberId);

    @EntityGraph(attributePaths = {"community"})
    List<CommunityMember> findByMember(Member member);

    @EntityGraph(attributePaths = {"community"})
    Page<CommunityMember> findByMember(Member member, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Page<CommunityMember> findByCommunityId(Long communityId, Pageable pageable);
}