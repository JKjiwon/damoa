package hello.sns.repository;

import hello.sns.domain.community.Community;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchCommunityRepositoryImplTest {

    @Autowired
    private CommunityRepository communityRepository;


    @Test
    public void testSearch() {

    }

}