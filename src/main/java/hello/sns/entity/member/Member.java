package hello.sns.entity.member;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.community.Community;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
		uniqueConstraints = {
				@UniqueConstraint(
						columnNames = {
								"email"
						})
		}
)
@Entity
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;

	private String password;

	private String name;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	public void addRole(MemberRole role) {
		this.role = role;
	}

	public void passwordEncoding(String password) {
		this.password = password;
	}
}


//	private String profileImageName;
//	private String profileImageUrl;
//	private String profileMessage;
