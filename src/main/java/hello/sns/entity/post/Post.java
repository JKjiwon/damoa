package hello.sns.entity.post;

import static javax.persistence.FetchType.*;

import javax.persistence.*;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.community.Community;
import hello.sns.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	@Builder
	public Post(String title, String content, Member writer, Community community) {
		this.title = title;
		this.content = content;
		this.writer = writer;
		this.community = community;
	}
}
