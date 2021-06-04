package hello.sns.entity.community;

import static javax.persistence.FetchType.*;

import javax.persistence.*;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.member.Member;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
public class CommunityMember extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "community_member_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "community_id")
	private Community community;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Enumerated(EnumType.STRING)
	private MemberGrade memberGrade;

	protected CommunityMember(Community community, Member member, MemberGrade memberGrade) {
		this.community = community;
		this.member = member;
		this.memberGrade = memberGrade;
	}

	// static 생성 메서드
	public static CommunityMember of(Member member, Community community, MemberGrade memberGrade) {
		CommunityMember communityMember = new CommunityMember(community, member, memberGrade);
		community.addCommunityMembers(communityMember);
		return communityMember;
	}

	public void changeMemberGrade(MemberGrade memberGrade) {
		this.memberGrade = memberGrade;
	}
}