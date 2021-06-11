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
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id", callSuper = false)
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

	@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
	private List<Comment> child = new ArrayList<>();

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	@Builder
	public Comment(String content, Member writer, Post post, Comment parent, Integer level) {
		this.content = content;
		this.writer = writer;
		this.post = post;
		this.parent = parent;
		this.level = level;
	}

	public boolean writtenBy(Member member) {
		return this.writer.equals(member);
	}

	public boolean existsChild() {
		return !child.isEmpty();
	}

	public void addComment(Comment comment) {
		this.child.add(comment);
	}

	public void update(String content) {
		this.content = content;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
