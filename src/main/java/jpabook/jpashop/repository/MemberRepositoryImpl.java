package jpabook.jpashop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.QMember;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static jpabook.jpashop.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findAllOrderByDesc() {
        return queryFactory
                .selectFrom(member)
                .orderBy()
                .fetch();
    }

    @Override
    public List<Member> findByName(String name) {
        return queryFactory
                .selectFrom(member)
                .where(member.name.eq(name))
                .fetch();
    }
}
