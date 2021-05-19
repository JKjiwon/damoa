package hello.sns.domain.community;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(
	uniqueConstraints = {
		@UniqueConstraint(
			name = "community_name_unique",
			columnNames = {"name"}
		)
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private Member owner;

	@Builder
	public Community(String name, String thumbNailImagePath, String mainImagePath, String introduction,
		Member owner) {
		this.name = name;
		this.thumbNailImagePath = thumbNailImagePath;
		this.mainImagePath = mainImagePath;
		this.introduction = introduction;
		this.owner = owner;
	}
}
