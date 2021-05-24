package hello.sns.domain.member;

import hello.sns.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
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

	@OneToMany(mappedBy = "member")
	private List<MemberRole> memberRoles;


	@Builder
	public Member(String email, String password, String name, String phoneNumber, LocalDate birthDate,
				  Sex sex, String profileImageName, String profileImageUrl, String profileMessage,
				  List<MemberRole> memberRoles) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.birthDate = birthDate;
		this.sex = sex;
		this.profileImageName = profileImageName;
		this.profileImageUrl = profileImageUrl;
		this.profileMessage = profileMessage;
		this.memberRoles = memberRoles;
	}
}
