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

import lombok.Getter;

@Entity
@Getter
@Table(
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"email", "nick_name"}
		)
	}
)

public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;  // loginId, unique

	private String password;

	private String nickName;

	private String phoneNumber;

	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	private Sex sex;

	private String profileImagePath;

	private String profileMessage;

	@Enumerated(EnumType.STRING)
	private Role role; // Spring Security 에 적용
}
