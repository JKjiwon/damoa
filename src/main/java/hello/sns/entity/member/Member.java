package hello.sns.entity.member;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.community.CommunityMember;
import hello.sns.web.dto.common.FileInfo;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	private String name;

	@Enumerated(EnumType.STRING)
	private MemberRole role = MemberRole.USER;

	@OneToMany(mappedBy = "member")
	private List<CommunityMember> communityMembers = new ArrayList<>();

	private String profileImageName;
	private String profileImagePath;
	private String profileMessage;

	public void updateProfileImage(FileInfo fileInfo) {
		this.profileImageName = fileInfo.getFileName();
		this.profileImagePath = fileInfo.getFilePath();
	}

	public void update(String name, String profileMessage) {
		this.name = name;
		this.profileMessage = profileMessage;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", email='" + email + '\'' +
				'}';
	}
}

