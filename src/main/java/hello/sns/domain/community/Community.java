package hello.sns.domain.community;

import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = {
		@UniqueConstraint(
			name = "community_name_unique",
			columnNames = {"name"}
		)
	}
)
@Entity
public class Community extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "community_id")
	private Long id;

	private String name;

	private String thumbNailImagePath;

	private String mainImagePath;

	private String introduction;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "admin_id")
	private Member admin;

	@Builder
	public Community(String name, String thumbNailImagePath, String mainImagePath, String introduction,
		Member admin) {
		this.name = name;
		this.thumbNailImagePath = thumbNailImagePath;
		this.mainImagePath = mainImagePath;
		this.introduction = introduction;
		this.admin = admin;
	}
}
