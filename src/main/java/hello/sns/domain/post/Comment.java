package hello.sns.domain.post;

import hello.sns.domain.BaseTimeEntity;
import hello.sns.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;

	private String content;

	private Integer level;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member writer;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> child = new ArrayList<>();

	@Builder
	public Comment(Long id, String content, Integer level, Member writer, Post post) {
		this.id = id;
		this.content = content;
		this.level = level;
		this.writer = writer;
		this.post = post;
	}

	public boolean writtenBy(Member member) {
		return this.writer.equals(member);
	}

	public void setParent(Comment parent) {
		this.parent = parent;
		parent.getChild().add(this);
	}

	public void update(String content) {
		this.content = content;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
