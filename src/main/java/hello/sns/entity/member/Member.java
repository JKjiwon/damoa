package hello.sns.entity.member;

import hello.sns.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = {
			@UniqueConstraint(
					columnNames = {
							"username"
					}),
			@UniqueConstraint(
					columnNames = {
							"email"
					})
	}
)
@Entity
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String username;

	private String password;

	private String email;

	private String name;

	@Enumerated(EnumType.STRING)
	private Role role;

//	private String profileImageName;
//	private String profileImageUrl;
//	private String profileMessage;

	@Builder
	public Member(String username, String password, String email, String name) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
	}

	public void changeRole(Role role) {
		this.role = role;
	}

	public void passwordEncoding(String password) {
		this.password = password;
	}

}
