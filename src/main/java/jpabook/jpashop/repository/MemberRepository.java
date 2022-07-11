package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// spring bean 에 자동등록
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // spring entity manager 주입 받을 수 있다. ( 자동주입 )
    //    @PersistenceContext
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // Entity 객체를 대상으로 조회할 수 있다. JPQL ( 기본편 참고 )
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where  m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
