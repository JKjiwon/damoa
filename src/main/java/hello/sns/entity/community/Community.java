package hello.sns.entity.community;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.category.Category;
import hello.sns.entity.member.Member;
import hello.sns.web.dto.common.FileInfo;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Community extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "community_id")
	private Long id;

	@Column(unique = true)
	private String name;

	private String introduction;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member owner;

	private String thumbNailImageName;

	private String thumbNailImagePath;

	private String mainImageName;

	private String mainImagePath;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	public void changeMainImage(FileInfo imageInfo) {
		mainImageName = imageInfo.getFileName();
		mainImagePath = imageInfo.getFilePath();
	}

	public void changeThumbNailImage(FileInfo imageInfo) {
		thumbNailImageName = imageInfo.getFileName();
		thumbNailImagePath = imageInfo.getFilePath();
	}

	public void changeOwner(Member owner) {
		this.owner = owner;
	}
}