package hello.sns.entity.community;

import hello.sns.entity.BaseTimeEntity;
import hello.sns.entity.member.Member;
import hello.sns.web.dto.community.UpdateCommunityDto;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Builder
@AllArgsConstructor
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

	/**
	 * 커뮤니티 생성
	 */


	public void update(UpdateCommunityDto updateCommunityDto) {
		introduction = updateCommunityDto.getIntroduction();
	}
}


//	private String thumbNailImageName;
//
//	private String thumbNailImageUrl;
//
//	private String mainImageName;
//
//	private String mainImageUrl;