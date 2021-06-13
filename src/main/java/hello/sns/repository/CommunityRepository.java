package hello.sns.repository;

import hello.sns.domain.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, SearchCommunityRepository{

    Boolean existsByName(String name);

    @EntityGraph(attributePaths = "category")
    Optional<Community> findById(Long id);

    @EntityGraph(attributePaths = "category")
    Page<Community> findAll(Pageable pageable);

//    @Query("SELECT m FROM Movie m WHERE m.title LIKE %:title%")

//    @Query(value = "select com from Community com" +
//            " join fetch com.category" +
//            " join fetch com.owner" +
//            " where com.name like %:keyword%" +
//            " or com.introduction like %:keyword%",
//    countQuery = "select count(com) from Community com" +
//            " join fetch com.category" +
//            " join fetch com.owner" +
//            " where com.name like %:keyword%" +
//            " or com.introduction like %:keyword%")
//    Page<Community> searchCommunity(@Param("keyword") String keyword, Pageable pageable);


}
