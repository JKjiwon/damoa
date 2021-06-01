package hello.sns.entity.community;

import static javax.persistence.FetchType.*;

import javax.persistence.*;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

	@Builder
	public CommunityMember(Community community, Member member, MemberGrade memberGrade) {
		this.community = community;
		this.member = member;
		this.memberGrade = memberGrade;
	}
}