package com.damoa.domain.member.entity;

import com.damoa.domain.BaseTimeEntity;
import com.damoa.web.dto.common.UploadFile;
import com.damoa.domain.member.dto.UpdateMemberDto;
import lombok.*;

import javax.persistence.*;

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

	public void updateProfileImage(UploadFile uploadFile) {
		this.profileImageName = uploadFile.getFileName();
		this.profileImagePath = uploadFile.getFilePath();
	}

	public void update(UpdateMemberDto dto) {
		this.name = dto.getName();
	}
}

