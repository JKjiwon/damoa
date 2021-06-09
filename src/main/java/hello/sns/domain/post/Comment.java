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
@EqualsAndHashCode(of = "id")
public class Comment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long id;

	private String content;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member writer;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	private boolean isHidden;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> children = new ArrayList<>();

	@Builder
	public Comment(String content, Member writer, Post post, Comment parent) {
		this.content = content;
		this.writer = writer;
		this.post = post;
		this.parent = parent;
		this.isHidden = false;
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	public boolean writtenBy(Member member) {
		return this.writer.equals(member);
	}

	public boolean existsChildren() {
		return !children.isEmpty();
	}

	public void addComment(Comment comment) {
		this.children.add(comment);
	}
}
