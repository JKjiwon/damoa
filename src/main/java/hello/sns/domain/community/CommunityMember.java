package hello.sns.domain.community;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.member.Member;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
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

	public CommunityMember(Community community, Member member, MemberGrade memberGrade) {
		this.community = community;
		this.member = member;
		this.memberGrade = memberGrade;
	}

	public void changeMemberGrade(MemberGrade memberGrade) {
		this.memberGrade = memberGrade;
	}

	public boolean isOwnerOrAdmin() {
		return this.memberGrade.equals(MemberGrade.OWNER) || this.memberGrade.equals(MemberGrade.ADMIN);
	}

	public boolean isOwner() {
		return this.memberGrade.equals(MemberGrade.OWNER);
	}
}