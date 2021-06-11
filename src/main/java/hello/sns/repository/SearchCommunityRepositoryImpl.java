package hello.sns.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import hello.sns.domain.category.QCategory;
import hello.sns.domain.community.Community;
import hello.sns.domain.community.QCommunity;
import hello.sns.domain.member.QMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Slf4j
public class SearchCommunityRepositoryImpl extends QuerydslRepositorySupport implements SearchCommunityRepository {

    public SearchCommunityRepositoryImpl() {
        super(Community.class);
    }

    @Override
    public Page<Community> search(String type, String keyword, Pageable pageable) {
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        QCommunity community = QCommunity.community;

        JPQLQuery<Community> jpqlQuery = from(community);
        jpqlQuery.leftJoin(community.owner).fetchJoin();
        jpqlQuery.leftJoin(community.category).fetchJoin();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = community.id.gt(0L);
        booleanBuilder.and(expression);

        if (type != null) {
            String[] typeArr = type.split("");
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t : typeArr) {
                switch (t) {
                    case "n":
                        conditionBuilder.or(community.name.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(community.category.name.contains(keyword));
                        break;
                    case "i":
                        conditionBuilder.or(community.introduction.contains(keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }
        jpqlQuery.where(booleanBuilder);

        // orderBy
        Sort sort = pageable.getSort();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            PathBuilder<Community> orderByExp = new PathBuilder<>(Community.class, "community");
            jpqlQuery.orderBy(new OrderSpecifier(direction, orderByExp.get(prop)));
        });

        jpqlQuery.offset(pageable.getOffset());
        jpqlQuery.limit(pageable.getPageSize());

        List<Community> result = jpqlQuery.fetch();
        long count = jpqlQuery.fetchCount();

        return new PageImpl<Community>(result, pageable, count);
    }
}


