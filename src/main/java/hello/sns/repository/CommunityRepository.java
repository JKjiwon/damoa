package hello.sns.repository;

import hello.sns.entity.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    Boolean existsByName(String name);
}
