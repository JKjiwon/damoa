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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "member_roles",
			joinColumns = @JoinColumn(name = "member_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

//	private String profileImageName;
//	private String profileImageUrl;
//	private String profileMessage;

	@Builder
	public Member(String username, String password, String email, String name, Set<Role> roles) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
		this.roles = roles;
	}

	public void passwordEncoding(String password) {
		this.password = password;
	}

	public void addRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
