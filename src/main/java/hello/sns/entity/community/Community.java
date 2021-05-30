package hello.sns.entity.community;

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

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.member.Member;
import hello.sns.service.CommunityUpdateDto;
import lombok.*;

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
@EqualsAndHashCode(of = "id")
public class Community extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "community_id")
	private Long id;

	private String name;

	private String thumbNailImageName;

	private String thumbNailImageUrl;

	private String mainImageName;

	private String mainImageUrl;

	private String introduction;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member admin;

	@Builder
	public Community(String name, String thumbNailImageName, String thumbNailImageUrl, String mainImageName, String mainImageUrl, String introduction, Member admin) {
		this.name = name;
		this.thumbNailImageName = thumbNailImageName;
		this.thumbNailImageUrl = thumbNailImageUrl;
		this.mainImageName = mainImageName;
		this.mainImageUrl = mainImageUrl;
		this.introduction = introduction;
		this.admin = admin;
	}

	public void update(CommunityUpdateDto communityUpdateDto) {
		introduction = communityUpdateDto.getIntroduction();
		mainImageName = communityUpdateDto.getMainImageName();
		mainImageUrl = communityUpdateDto.getMainImageUrl();
		thumbNailImageName = communityUpdateDto.getThumbNailImageName();
		thumbNailImageUrl = communityUpdateDto.getThumbNailImageUrl();
	}
}
