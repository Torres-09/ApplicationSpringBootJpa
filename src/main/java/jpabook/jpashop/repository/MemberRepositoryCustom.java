package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    public List<Member> findAllOrderByDesc();

    public List<Member> findByName(String name);
}
