package hello.sns.domain.post;

import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.community.Community;
import hello.sns.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@JoinColumn(name = "author_id")
	private Member author;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "community_id")
	private Community community;

	@Builder
	public Post(String title, String content, Member author, Community community) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.community = community;
	}
}
