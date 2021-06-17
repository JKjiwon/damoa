package hello.sns.domain.member;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.community.CommunityMember;
import hello.sns.web.dto.common.CurrentMember;
import hello.sns.web.dto.common.FileInfo;
import hello.sns.web.dto.member.UpdateMemberDto;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	private String profileImageName;
	private String profileImagePath;

	public void updateProfileImage(FileInfo fileInfo) {
		this.profileImageName = fileInfo.getFileName();
		this.profileImagePath = fileInfo.getFilePath();
	}

	public void update(UpdateMemberDto dto) {
		this.name = dto.getName();
	}
}

