package hello.sns.entity.member;

import hello.sns.entity.BaseTimeEntity;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	private MemberRole role;

	private String profileImageName;
	private String profileImagePath;
	private String profileMessage;

	public Member(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = MemberRole.USER;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void updateProfileImage(String profileImageName, String profileImageUrl) {
		this.profileImageName = profileImageName;
		this.profileImagePath = profileImageUrl;
	}

	public void update(String name, String profileMessage) {
		this.name = name;
		this.profileMessage = profileMessage;
	}
}

