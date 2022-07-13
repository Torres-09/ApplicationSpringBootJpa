package jpabook.jpashop.service;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
@RunWith(SpringRunner.class) // Junit4 에서 쓰는 spring 과 같이 test 할 때 쓰는
@SpringBootTest // spring 컨테이너 안에서 test
@Transactional // test 이후에 rollback
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;


    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");
        //When
        Long saveId = memberService.join(member);
        //Then
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
    }
    
    @Test(expected = IllegalStateException.class) // try catch 대신에 이것을 쓸 수 있음
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("lim");

        Member member2 = new Member();
        member2.setName("lim");

        //when
        memberService.join(member1);
        memberService.join(member2);

        //then
        // 여기에 도달하면 안된다!!
        fail("예외가 발생해야 한다.");
    }
}