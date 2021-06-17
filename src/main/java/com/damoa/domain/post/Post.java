package com.damoa.domain.post;

import com.damoa.domain.BaseTimeEntity;
import com.damoa.domain.community.Community;
import com.damoa.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	public void addImages(Image image) {
		this.images.add(image);
		image.setPost(this);
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
