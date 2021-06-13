package hello.sns.repository;

import hello.sns.domain.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchCommunityRepository {

    Page<Community> findAllSearch(String type, String keyword, Pageable pageable);
}
