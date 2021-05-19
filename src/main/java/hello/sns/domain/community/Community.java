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

	private String thumbNailImageName;

	private String thumbNailImagePath;

	private String mainImageName;

	private String mainImageUrl;

	private String introduction;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "admin_id")
	private Member admin;

	@Builder
	public Community(String name, String thumbNailImageName, String thumbNailImagePath, String mainImageName,
		String mainImageUrl, String introduction, Member admin) {
		this.name = name;
		this.thumbNailImageName = thumbNailImageName;
		this.thumbNailImagePath = thumbNailImagePath;
		this.mainImageName = mainImageName;
		this.mainImageUrl = mainImageUrl;
		this.introduction = introduction;
		this.admin = admin;
	}
}
