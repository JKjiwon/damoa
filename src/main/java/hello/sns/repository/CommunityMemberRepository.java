package hello.sns.repository;

import hello.sns.entity.community.Community;
import hello.sns.entity.community.CommunityMember;
import hello.sns.entity.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    Boolean existsByMemberAndCommunity(Member member, Community community);

    Optional<CommunityMember> findByMemberAndCommunity(
            Member member, Community community);


    @EntityGraph(attributePaths = {"community", "member"})
    List<CommunityMember> findByMember(@Param("member") Member member);
}

//    @Query("select cm" +
//            " from CommunityMember cm" +
//            " where cm.member=:member and cm.community=:community")
//    Optional<CommunityMember> findByMemberAndCommunity(
//            @Param("member") Member member, @Param("community") Community community);

