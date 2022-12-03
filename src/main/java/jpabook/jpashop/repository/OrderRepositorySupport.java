package jpabook.jpashop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.QMember;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public OrderRepositorySupport(JPAQueryFactory queryFactory) {
        super(Order.class);
        this.queryFactory = queryFactory;
    }
}