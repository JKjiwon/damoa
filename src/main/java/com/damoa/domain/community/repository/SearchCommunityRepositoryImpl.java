package com.damoa.domain.community.repository;

import com.damoa.domain.community.entity.QCommunity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPQLQuery;
import com.damoa.domain.community.entity.Community;
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
    public Page<Community> findAllSearch(String type, String keyword, Pageable pageable) {

        keyword = getKeyword(keyword);

        QCommunity community = QCommunity.community;

        JPQLQuery<Community> jpqlQuery = from(community);
        jpqlQuery.leftJoin(community.owner).fetchJoin();
        jpqlQuery.leftJoin(community.category).fetchJoin();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = community.id.gt(0L);
        booleanBuilder.and(expression);

        if (type != null && !keyword.equals("")) {
            String[] typeArr = type.split("");
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t : typeArr) {
                switch (t) {
                    case "n":
                        conditionBuilder.or(getContains(community.name, keyword));
                        break;
                    case "c":
                        conditionBuilder.or(getContains(community.category.name, keyword));
                        break;
                    case "i":
                        conditionBuilder.or(getContains(community.introduction, keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }
        jpqlQuery.where(booleanBuilder);

        // Order 처리
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            PathBuilder<Community> orderByExp = new PathBuilder<>(Community.class, "community");
            jpqlQuery.orderBy(new OrderSpecifier(direction, orderByExp.get(prop)));
        });

        // Paging 처리
        jpqlQuery.offset(pageable.getOffset());
        jpqlQuery.limit(pageable.getPageSize());

        List<Community> result = jpqlQuery.fetch();
        long count = jpqlQuery.fetchCount();

        return new PageImpl<Community>(result, pageable, count);
    }

    private BooleanExpression getContains(StringPath name, String keyword) {
        return name.toLowerCase().contains(keyword);
    }

    private String getKeyword(String keyword) {
        if (keyword == null) {
            keyword = "";
        } else {
            keyword = keyword.trim().toLowerCase();
        }
        System.out.println("keyword = " + keyword);
        return keyword;
    }
}


