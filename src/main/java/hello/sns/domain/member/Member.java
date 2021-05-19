package hello.sns.domain.member;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import hello.sns.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = {
		@UniqueConstraint(
			name = "member_email_name_unique",
			columnNames = {"email", "name"}
		)
	}
)
@Entity
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;  // loginId, unique

	private String password;

	private String name;

	private String phoneNumber;

	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	private Sex sex;

	private String profileImageName;

	private String profileImageUrl;

	private String profileMessage;

	@Enumerated(EnumType.STRING)
	private Role role; // Spring Security 에 적용

	public Member(String email, String password, String name, String phoneNumber, LocalDate birthDate,
		Sex sex, String profileImageName, String profileImageUrl, String profileMessage, Role role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.birthDate = birthDate;
		this.sex = sex;
		this.profileImageName = profileImageName;
		this.profileImageUrl = profileImageUrl;
		this.profileMessage = profileMessage;
		this.role = role;
	}
}
