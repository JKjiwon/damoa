package com.damoa.domain.community;

import com.damoa.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommunityMember{

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

	@CreatedDate
	private LocalDateTime joinedAt;

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

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