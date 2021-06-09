package hello.sns.domain.community;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.category.Category;
import hello.sns.domain.member.Member;
import hello.sns.web.dto.common.FileInfo;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@EqualsAndHashCode(of = "id")
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
	@JoinColumn(name = "owner_id")
	private Member owner;

	private String thumbNailImageName;

	private String thumbNailImagePath;

	private String mainImageName;

	private String mainImagePath;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<CommunityMember> communityMembers = new ArrayList<>();

	public void join(Member member, MemberGrade memberGrade) {
		CommunityMember communityMember = new CommunityMember(this, member, memberGrade);
		getCommunityMembers().add(communityMember);
	}

	public void withdraw(CommunityMember communityMember) {
		getCommunityMembers().remove(communityMember);
	}

	public void changeMainImage(FileInfo imageInfo) {
		mainImageName = imageInfo.getFileName();
		mainImagePath = imageInfo.getFilePath();
	}

	public void changeThumbNailImage(FileInfo imageInfo) {
		thumbNailImageName = imageInfo.getFileName();
		thumbNailImagePath = imageInfo.getFilePath();
	}

	@Builder
	protected Community(Long id, String name, String introduction, Member owner, Category category) {
		this.id = id;
		this.name = name;
		this.introduction = introduction;
		this.owner = owner;
		this.category = category;
	}

	public void changeCategory(Category category) {
		this.category = category;
	}

	public void update(String introduction, Category category) {
		this.introduction = introduction;
		this.category = category;
	}
}