package hello.sns.domain.post;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
public class Post extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "post_id")
	private Long id;

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
	public Post(String content, Member writer, Community community) {
		this.content = content;
		this.writer = writer;
		this.community = community;
	}

	public boolean writtenBy(Member member) {
		return writer.equals(member);
	}
}
