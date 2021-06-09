package hello.sns.entity.post;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import hello.sns.service.FileService;
import hello.sns.service.ImageService;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EqualsAndHashCode(of = "id")
public class Post extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "post_id")
	private Long id;

	private String title;

	private String content;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member writer;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "community_id")
	private Community community;

	@OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
	private List<Image> images = new ArrayList<>();

	public void addImages(Image image) {
		this.images.add(image);
	}

	@Builder
	public Post(String title, String content, Member writer, Community community) {
		this.title = title;
		this.content = content;
		this.writer = writer;
		this.community = community;
	}

	public boolean writtenBy(Member member) {
		return this.writer.equals(member);
	}

	public void deleteImages(FileService fileService, ImageService imageService) {
		images.stream()
				.map(Image::getPath)
				.forEach(fileService::deleteFile);

		imageService.deletePostAllImages(this.id);
	}
}
